package host.galactic.stellar.rest;

import host.galactic.data.repositories.VotingRepository;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@RequestScoped
public class StellarVotingRestDelete {
    @Inject
    VotingRepository votingRepository;

    @Inject
    UserInfo userInfo;

    public Uni<Response> byId(long id) {
        Log.infof("Got request to delete voting with id = %s", id);

        return votingRepository.deleteById(id, userInfo.getEmail())
                .map(v -> Response.noContent().build())
                .onFailure()
                .invoke(() -> Log.warnf("Could not delete voting with id = %s", id));
    }
}
