package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.UserRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.core.Response;

@RequestScoped
class StellarVotingRestVotersOperations {
    @Inject
    UserRepository userRepository;

    @Inject
    VotingRepository votingRepository;

    @Inject
    UserInfo userInfo;

    @WithTransaction
    public Uni<Response> addVoters(Long votingId, AddVotersRequest addVotersRequest) {
        Log.info("addVoters(): Got request to add voters to voting.");
        Log.debugf("addVoters(): Request details: votingId = %s, addVotersRequest = %s", votingId, addVotersRequest);

        return votingRepository.getById(votingId)
                .invoke(this::checkIfUserIsAllowedToAddVotersTo)
                .replaceWith(userRepository.createIfNotExist(addVotersRequest.emails()))
                .onItem()
                .transformToUni(v -> votingRepository.addVotersTo(votingId, v))
                .map(l -> Response.noContent().build())
                .onFailure()
                .invoke(t -> Log.warn("addVoters(): Could not add voters!", t));
    }

    private void checkIfUserIsAllowedToAddVotersTo(VotingEntity voting) {
        if (!voting.createdBy.email.equals(userInfo.getEmail())) {
            Log.warnf("Adding voters failed. User \"%s\" is not the owner of voting = %s.", userInfo.getEmail(), voting.id);
            throw new ForbiddenException();
        }
    }
}
