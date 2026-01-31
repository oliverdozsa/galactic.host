package host.galactic.stellar.rest;

import host.galactic.data.entities.ChannelAccountEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.commission.CommissionCreateTransactionRequest;
import host.galactic.stellar.rest.responses.commission.CommissionCreateTransactionResponse;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.BadRequestException;
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
    @Named("signing")
    AsymmetricCipherKeyPair signingKey;

    public Uni<CommissionCreateTransactionResponse> create(CommissionCreateTransactionRequest request) {
        Log.infof("Got request to create transaction for: %s", toLoggableString(request));
        var parsedMessage = ParsedMessage.parse(request.message());

        return verifySignatureOf(request)
                .onItem().transformToUni(v -> votingRepository.getById(parsedMessage.votingId))
                .invoke(this::checkAssetAccountsCreated)
                .onItem()
                .transformToUni(v -> consumeAChannelFor(v))
                .onItem()
                .transformToUni(c -> createTransaction(c, parsedMessage.voterPublic))
                .onItem()
                .transform(this::toResponse);


        // TODO: 1. Check that signature is valid for message
        // TODO: 2. Parse the message. It's in format: votingId|voterPublic. It's assumed that voterPublic doesn't exist
        // TODO: 3. Check that voting exists
        // TODO: 4. Ensure that asset accounts are created for voting
        // TODO: 5. Consume a channel account from voting
        // TODO: 6. Using the channel account, the distribution account, the issuer account and the voter public, form the transaction,
        // TODO     that creates voter account, and transfers 1 vote token to it.
        // TODO: 7. Return the transaction as envelopeXdrBase64
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

    private void checkAssetAccountsCreated(VotingEntity voting) {
        if (!VotingChecks.areAssetAccountsCreated(voting)) {
            Log.warnf("Asset accounts are not ready for voting: %s", voting.id);
            throw new ServiceUnavailableException("Asset accounts are not ready for voting:" + voting.id);
        }
    }

    private Uni<ChannelAccountEntity> consumeAChannelFor(VotingEntity voting) {
        // TODO
        return null;
    }

    private Uni<String> createTransaction(ChannelAccountEntity channelAccountEntity, String voterPublic) {
        // TODO
        return null;
    }

    private CommissionCreateTransactionResponse toResponse(String transaction) {
        // TODO
        return null;
    }

    private class ParsedMessage {
        Long votingId;
        String voterPublic;

        static ParsedMessage parse(String message) {
            // TODO
            return null;
        }

        private ParsedMessage(String rawMessage) {
            // TODO
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
