package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.EnvelopeSignatureRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.commission.CommissionSignEnvelopeRequest;
import host.galactic.stellar.rest.responses.commission.CommissionSignEnvelopeResponse;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.hibernate.reactive.mutiny.Mutiny;

import java.time.Instant;
import java.util.Base64;

import static host.galactic.stellar.rest.VotingChecks.doesUserNotParticipateIn;

@RequestScoped
public class StellarCommissionRestSignEnvelope {
    @Inject
    UserInfo userInfo;

    @Inject
    VotingRepository votingRepository;

    @Inject
    EnvelopeSignatureRepository signatureRepository;

    @Inject
    @Named("signing")
    AsymmetricCipherKeyPair signingKey;

    @WithTransaction
    public Uni<CommissionSignEnvelopeResponse> sign(Long votingId, CommissionSignEnvelopeRequest request) {
        Log.infof("Got request to sign envelope of user: %s for voting: %s", userInfo.getEmail(), votingId);

        return votingRepository.getById(votingId)
                .onItem()
                .call(v -> Mutiny.fetch(v.voters))
                .invoke(this::checkIfVotingHasStartedButNotEndedAlready)
                .invoke(this::checkIfUserIsAllowedToSignEnvelope)
                .call(this::checkIfUserSignedAnEnvelopeAlready)
                .onItem()
                .transformToUni(v -> createResponse(request, v));
    }

    @WithSession
    public Uni<CommissionSignEnvelopeResponse> getBy(Long votingId) {
        Log.infof("Got request to get envelope signature of user: %s for voting: %s", userInfo.getEmail(), votingId);
        return signatureRepository.findFor(votingId, userInfo.getEmail())
                .onFailure(NoResultException.class)
                .transform(t -> new NotFoundException())
                .onItem()
                .transform(e -> new CommissionSignEnvelopeResponse(e.signature));
    }

    private void checkIfUserIsAllowedToSignEnvelope(VotingEntity voting) {
        if(doesUserNotParticipateIn(voting, userInfo.getEmail())){
            throw new ForbiddenException("User does not participate in this voting.");
        }
    }

    private Uni<VotingEntity> checkIfUserSignedAnEnvelopeAlready(VotingEntity voting) {
        return signatureRepository.findFor(voting.id, userInfo.getEmail())
                .onItem()
                .failWith(() -> new ForbiddenException())
                .onFailure(NoResultException.class)
                .recoverWithNull()
                .replaceWith(voting);
    }

    private Uni<CommissionSignEnvelopeResponse> createResponse(CommissionSignEnvelopeRequest request, VotingEntity voting) {
        var signature = createSignatureAsBase64(request);
        return signatureRepository.create(signature, voting.id, userInfo.getEmail())
                .map(entity -> new CommissionSignEnvelopeResponse(entity.signature));
    }

    private String createSignatureAsBase64(CommissionSignEnvelopeRequest request) {
        var rsaEngine = new RSAEngine();
        rsaEngine.init(true, signingKey.getPrivate());

        var envelopeBytes = Base64.getDecoder().decode(request.envelopeBase64());
        var signatureBytes = rsaEngine.processBlock(envelopeBytes, 0, envelopeBytes.length);

        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    private void checkIfVotingHasStartedButNotEndedAlready(VotingEntity voting) {
        var now = Instant.now();
        if(voting.startDate.isAfter(now)) {
            throw new ForbiddenException("Voting has not started yet!");
        }

        if(voting.endDate.isBefore(now)) {
            throw new ForbiddenException("Voting ended!");
        }
    }
}
