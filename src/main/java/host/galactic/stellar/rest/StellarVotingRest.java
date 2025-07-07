package host.galactic.stellar.rest;

import host.galactic.data.repositories.UserRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import io.quarkus.logging.Log;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/stellar/votings")
public class StellarVotingRest {
    @Inject
    StellarVotingRestCreateVotings votingRestCreate;

    @Inject
    StellarVotingRestGetVotings votingRestGet;

    @Inject
    StellarVotingRestVotersOperations votingRestVotersOperations;

    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(@Valid CreateVotingRequest createVotingRequest) {
        return votingRestCreate.create(createVotingRequest);
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<VotingResponse> get(Long id) {
        return votingRestGet.byId(id);
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

    @Path("/addvoters/{votingId}")
    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addVoters(Long votingId, @Valid AddVotersRequest addVotersRequest) {
        return votingRestVotersOperations.addVoters(votingId, addVotersRequest);
    }
}
