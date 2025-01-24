package host.galactic.requests;

import host.galactic.stellar.rest.requests.CreateVotingRequest;
import io.quarkus.logging.Log;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/stellar/votings")
public class StellarVotingRest {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void create(@Valid CreateVotingRequest createVotingRequest) {
        Log.info("create()");
        Log.debugf("create(): createVotingRequest = %s", createVotingRequest.toString());
        // TODO
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
