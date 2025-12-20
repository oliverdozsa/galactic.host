package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.commission.CommissionInitRequest;
import host.galactic.stellar.rest.responses.commission.CommissionInitResponse;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.hibernate.reactive.mutiny.Mutiny;

@RequestScoped
class StellarCommissionInitSessionRest {
    @Inject
    UserInfo userInfo;

    @Inject
    VotingRepository votingRepository;

    @WithSession
    public Uni<CommissionInitResponse> initSession(CommissionInitRequest request) {
        Log.infof("Got request to init a session.");

        return votingRepository.getById(request.votingId())
                .call(v -> Mutiny.fetch(v.voters))
                .invoke(this::checkIfUserIsAllowedToInitSession)
                .map(v -> new CommissionInitResponse(null)); // TODO
    }

    private void checkIfUserIsAllowedToInitSession(VotingEntity voting) {
        // TODO
    }
}
