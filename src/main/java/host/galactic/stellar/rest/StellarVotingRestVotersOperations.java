package host.galactic.stellar.rest;

import host.galactic.data.repositories.UserRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
class StellarVotingRestVotersOperations {
    @Inject
    UserRepository userRepository;

    @Inject
    VotingRepository votingRepository;

    @WithTransaction
    public Uni<Response> addVoters(Long votingId, AddVotersRequest addVotersRequest) {
        Log.info("addVoters(): Got request to add voters to voting.");
        Log.debugf("addVoters(): Request details: votingId = %s, addVotersRequest = %s", votingId, addVotersRequest);

        return userRepository.createIfNotExist(addVotersRequest.emails())
                .onItem()
                .transformToUni(v -> votingRepository.addVotersTo(votingId, v))
                .map(l -> Response.noContent().build())
                .onFailure()
                .invoke(t -> Log.warn("addVoters(): Could not add voters!", t));
    }
}
