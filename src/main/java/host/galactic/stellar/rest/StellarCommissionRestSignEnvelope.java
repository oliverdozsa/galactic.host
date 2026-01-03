package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.EnvelopeSignatureRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.commission.CommissionSignEnvelopeRequest;
import host.galactic.stellar.rest.responses.commission.CommissionSignEnvelopeResponse;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.ForbiddenException;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.hibernate.reactive.mutiny.Mutiny;

import static host.galactic.stellar.rest.VotingChecks.doesUserNotParticipateIn;

@RequestScoped
public class StellarCommissionRestSignEnvelope {
    @Inject
    private UserInfo userInfo;

    @Inject
    private VotingRepository votingRepository;

    @Inject
    private EnvelopeSignatureRepository signatureRepository;

    @Inject
    @Named("signing")
    private AsymmetricCipherKeyPair signingKey;

    @WithTransaction
    public Uni<CommissionSignEnvelopeResponse> sign(Long votingId, CommissionSignEnvelopeRequest request) {
        Log.infof("Got request to sign envelope of user: %s for voting: %s", userInfo.getEmail(), votingId);

        return votingRepository.getById(votingId)
                .onItem()
                .call(v -> Mutiny.fetch(v.voters))
                .invoke(this::checkIfUserIsAllowedToSignEnvelope)
                .call(this::checkIfUserSignedAnEnvelopeAlready)
                .onItem()
                .transformToUni(v -> createResponse(request, v));
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
                .replaceWith(voting);
    }

    private Uni<CommissionSignEnvelopeResponse> createResponse(CommissionSignEnvelopeRequest request, VotingEntity voting) {
        // TODO: produce signature, persist it, then create response
        return null;
    }
}
