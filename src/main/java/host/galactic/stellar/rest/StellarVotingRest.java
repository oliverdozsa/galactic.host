package host.galactic.stellar.rest;

import host.galactic.data.repositories.UserRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.operations.StellarInternalFundingAccount;
import host.galactic.stellar.operations.StellarOperationsProducer;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
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
    VotingRepository votingRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    StellarVotingRestCreateVotings stellarVotingRestCreate;

    @Inject
    StellarVotingRestGetVotings stellarVotingRestGetVotings;

    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(@Valid CreateVotingRequest createVotingRequest) {
        return stellarVotingRestCreate.create(createVotingRequest);
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<VotingResponse> get(Long id) {
        return stellarVotingRestGetVotings.get(id);
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
