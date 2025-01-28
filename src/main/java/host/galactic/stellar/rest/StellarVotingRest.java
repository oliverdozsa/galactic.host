package host.galactic.stellar.rest;

import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.createvoting.CreateVotingRequest;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;

@Path("/stellar/votings")
public class StellarVotingRest {
    @Inject
    VotingRepository repository;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(@Valid CreateVotingRequest createVotingRequest) {
        Log.info("create()");
        Log.debugf("create(): createVotingRequest = %s", createVotingRequest.toString());

        return repository.createFrom(createVotingRequest)
                .map(e -> {
                    URI entityId = UriBuilder.fromMethod(StellarVotingRest.class, "get")
                            .build(e.id);
                    return Response.created(entityId).build();
                });
    }

    @Path("/{id}")
    @GET
    public void get(String id) {
        // TODO
    }

    @Path("/of-vote-caller")
    @GET
    public void getOfVoteCaller() {
        // TODO
    }

    @Path("/of-voter")
    @GET
    public void getOfVoter() {
        // TODO
    }
}
