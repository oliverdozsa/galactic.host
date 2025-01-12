package host.galactic.stellar.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/stellar/votings")
public class StellarVotingRest {
    @POST
    public void create() {
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
