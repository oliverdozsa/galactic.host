package host.galactic.stellar.rest;

import host.galactic.data.entities.ChannelAccountEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.ChannelAccountRepository;
import host.galactic.data.repositories.VoterTransactionRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.operations.StellarCreateVoterAccountTxPayload;
import host.galactic.stellar.operations.StellarOperationsProducer;
import host.galactic.stellar.rest.requests.commission.CommissionCreateTransactionRequest;
import host.galactic.stellar.rest.requests.commission.CommissionGetTransactionOfSignatureRequest;
import host.galactic.stellar.rest.responses.commission.CommissionCreateTransactionResponse;
import host.galactic.stellar.rest.responses.commission.CommissionGetTransactionOfSignatureResponse;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ServiceUnavailableException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.signers.PSSSigner;

import java.util.Base64;

@RequestScoped
public class StellarCommissionRestTransaction {
    @Inject
    VotingRepository votingRepository;

    @Inject
    ChannelAccountRepository channelAccountRepository;

    @Inject
    VoterTransactionRepository voterTransactionRepository;

    @Inject
    StellarOperationsProducer stellarOperationsProducer;

    @Inject
    @Named("signing")
    AsymmetricCipherKeyPair signingKey;

    @WithTransaction
    public Uni<CommissionCreateTransactionResponse> create(CommissionCreateTransactionRequest request) {
        Log.infof("Got request to create transaction for: %s", toLoggableString(request));
        var parsedMessage = ParsedMessage.parse(request.message());

        return verifySignatureOf(request)
                .onItem()
                .call(() -> checkIfTxExistsFor(request.revealedSignatureBase64()))
                .onItem().transformToUni(v -> votingRepository.getById(parsedMessage.votingId))
                .onFailure(NotFoundException.class).transform(e -> new BadRequestException())
                .invoke(this::checkAssetAccountsCreated)
                .onItem()
                .transformToUni(v -> channelAccountRepository.consumeOneFor(v.id))
                .onFailure(NotFoundException.class).transform(e -> new ServiceUnavailableException())
                .onItem()
                .transformToUni(c -> createTransaction(c, parsedMessage.voterPublic))
                .onItem()
                .transformToUni(t -> saveTransactionFor(request.revealedSignatureBase64(), t))
                .onItem()
                .transform(this::toResponse);
    }

    @WithSession
    public Uni<CommissionGetTransactionOfSignatureResponse> getTxOfSignature(CommissionGetTransactionOfSignatureRequest request) {
        return voterTransactionRepository.findBySignature(request.signature())
                .onItem()
                .ifNull()
                .failWith(new NotFoundException())
                .onItem()
                .transform(e -> toGetTxResponse(e.transaction));
    }

    private Uni<Void> verifySignatureOf(CommissionCreateTransactionRequest request) {
        return Uni.createFrom().voidItem().invoke(() -> {
            var messageBytes = request.message().getBytes();
            var signatureBytes = Base64.getDecoder().decode(request.revealedSignatureBase64());

            var signer = new PSSSigner(new RSAEngine(), new SHA384Digest(), 0);
            signer.init(false, signingKey.getPublic());

            signer.update(messageBytes, 0, messageBytes.length);
            if (signer.verifySignature(signatureBytes)) {
                Log.infof("Signature is valid for request: %s", toLoggableString(request));
            } else {
                Log.warnf("Invalid signature for request: %s", toLoggableString(request));
                throw new BadRequestException("Signature is invalid!");
            }
        });
    }

    private Uni<Void> checkIfTxExistsFor(String signature) {
        return voterTransactionRepository.findBySignature(signature)
                .onItem()
                .ifNotNull()
                .failWith(new BadRequestException())
                .replaceWithVoid();
    }

    private void checkAssetAccountsCreated(VotingEntity voting) {
        if (!VotingChecks.areAssetAccountsCreated(voting)) {
            Log.warnf("Asset accounts are not ready for voting: %s", voting.id);
            throw new ServiceUnavailableException("Asset accounts are not ready for voting:" + voting.id);
        }
    }

    private Uni<String> createTransaction(ChannelAccountEntity channelAccountEntity, String voterPublic) {
        var stellarOperation = stellarOperationsProducer.create(channelAccountEntity.voting.isOnTestNetwork);
        var payload = new StellarCreateVoterAccountTxPayload(
                channelAccountEntity.accountSecret,
                channelAccountEntity.voting.distributionAccountSecret,
                channelAccountEntity.voting.issuerAccountSecret,
                channelAccountEntity.voting.assetCode,
                channelAccountEntity.voting.maxVoters,
                voterPublic
        );

        return stellarOperation.createVoterAccountTransaction(payload);
    }

    private Uni<String> saveTransactionFor(String signature, String transaction) {
        return voterTransactionRepository.createFrom(signature, transaction)
                .onItem()
                .transform(e -> e.transaction);
    }

    private CommissionCreateTransactionResponse toResponse(String transaction) {
        return new CommissionCreateTransactionResponse(transaction);
    }

    private CommissionGetTransactionOfSignatureResponse toGetTxResponse(String transaction) {
        return new CommissionGetTransactionOfSignatureResponse(transaction);
    }

    private static class ParsedMessage {
        Long votingId;
        String voterPublic;

        private ParsedMessage(Long votingId, String voterPublic) {
            this.votingId = votingId;
            this.voterPublic = voterPublic;
        }

        static ParsedMessage parse(String message) {
            var parts = message.split("\\|");
            var votingId = Long.parseLong(parts[0]);
            var voterPublic = parts[1];

            return new ParsedMessage(votingId, voterPublic);
        }
    }

    private static String toLoggableString(CommissionCreateTransactionRequest request) {
        var message = request.message();
        var truncatedMessage = message.length() >= 10 ? message.substring(0, 10) + "..." : message;

        var signature = request.revealedSignatureBase64();
        var truncatedSignature = signature.length() > 10 ? signature.substring(0, 10) + "..." : signature;

        return "message = " + truncatedMessage + ", signature = " + truncatedSignature;
    }
}
