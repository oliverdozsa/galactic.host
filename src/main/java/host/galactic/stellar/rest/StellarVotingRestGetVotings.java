package host.galactic.stellar.rest;

import host.galactic.data.entities.Visibility;
import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.rest.mappers.VotingEntityMapper;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.hibernate.reactive.mutiny.Mutiny;

@RequestScoped
class StellarVotingRestGetVotings {
    @Inject
    VotingRepository votingRepository;

    @Inject
    UserInfo userInfo;

    public Uni<VotingResponse> get(Long id) {
        Log.infof("get(): Got request to get voting by id = %s", id);

        return votingRepository.getById(id)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException())
                .onItem()
                .call(e -> Mutiny.fetch(e.voters))
                .invoke(e -> checkIfUserIsAllowedToGetVoting(e, userInfo.getEmail()))
                .map(VotingEntityMapper::from)
                .onFailure()
                .invoke(t -> Log.warn("get(): Could not get voting!", t));
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
