package host.galactic.stellar.rest;

import host.galactic.data.entities.Visibility;
import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.mappers.VotingEntityMapper;
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

import java.net.URI;

@Path("/stellar/votings")
public class StellarVotingRest {
    @Inject
    VotingRepository repository;

    @Inject
    JsonWebToken jwt;

    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<Response> create(@Valid CreateVotingRequest createVotingRequest) {
        Log.info("create()");
        Log.debugf("create(): user = \"%s\", createVotingRequest = %s", jwt.getName(), createVotingRequest.toString());

        return repository.createFrom(createVotingRequest, jwt.getName())
                .map(StellarVotingRest::toCreatedResponse);
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<VotingResponse> get(Long id) {
        Log.infof("get(): id = %s", id);

        return repository.getById(id)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException())
                .onItem()
                .invoke(e -> checkIfUserIsAllowedToGetVoting(e, jwt.getName()))
                .map(VotingEntityMapper::from);
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

    private void checkIfUserIsAllowedToGetVoting(VotingEntity entity, String user) {
        // TODO: check if user is participant
        if (entity.visibility == Visibility.PRIVATE) {
            Log.warnf("User \"%s\" is not allowed to get voting with id = %s", user, entity.id);
            throw new ForbiddenException();
        }
    }
}
