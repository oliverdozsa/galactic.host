package host.galactic.stellar.rest;

import host.galactic.data.entities.Visibility;
import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.UserRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.mappers.VotingEntityMapper;
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
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.hibernate.reactive.mutiny.Mutiny;

import java.net.URI;

@Path("/stellar/votings")
public class StellarVotingRest {
    @Inject
    VotingRepository votingRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    JsonWebToken jwt;

    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(@Valid CreateVotingRequest createVotingRequest) {
        Log.info("create(): Got request to create a voting.");
        Log.debugf("create(): Details of voting request: user = \"%s\", createVotingRequest = %s", jwt.getName(), createVotingRequest.toString());

        return userRepository.findByEmail(jwt.getClaim("email"))
                .onItem()
                .transformToUni(u -> votingRepository.createFrom(createVotingRequest, u))
                .map(StellarVotingRest::toCreatedResponse)
                .onFailure()
                .invoke(t -> Log.warn("create(): Could not create voting!", t));
    }

    @Path("/addvoters/{votingId}")
    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> addVoters(Long votingId, @Valid AddVotersRequest addVotersRequest) {
        Log.info("addVoters()");
        Log.debugf("addVoters(): votingId = %s, addVotersRequest = %s", votingId, addVotersRequest);

        return userRepository.createIfNotExist(addVotersRequest.emails())
                .onItem()
                .transformToUni(v -> votingRepository.addVotersTo(votingId, v))
                .map(l -> Response.noContent().build());
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<VotingResponse> get(Long id) {
        Log.infof("get(): Got request to get voting by id = %s", id);

        return votingRepository.getById(id)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException())
                .onItem()
                .call(e -> Mutiny.fetch(e.voters))
                .invoke(e -> checkIfUserIsAllowedToGetVoting(e, jwt.getClaim("email")))
                .map(VotingEntityMapper::from)
                .onFailure()
                .invoke(t -> Log.warn("get(): Could not get voting!", t));
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

    private static Response toCreatedResponse(VotingEntity entity) {
        URI entityId = UriBuilder
                .fromResource(StellarVotingRest.class)
                .path("/{id}")
                .build(entity.id);
        return Response.created(entityId).build();
    }

    private void checkIfUserIsAllowedToGetVoting(VotingEntity voting, String email) {
        if (voting.visibility == Visibility.PRIVATE && doesUserNotParticipateIn(voting, email)) {
            Log.warnf("User \"%s\" is not allowed to get voting with id = %s", email, voting.id);
            throw new ForbiddenException();
        }
    }

    private boolean doesUserNotParticipateIn(VotingEntity entity, String email) {
        return entity.voters.stream()
                .map(u -> u.email)
                .noneMatch(e -> e.equals(email));
    }
}
