package host.galactic.stellar.rest;

import host.galactic.data.entities.Visibility;
import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.data.utils.Page;
import host.galactic.stellar.rest.mappers.VotingEntityMapper;
import host.galactic.stellar.rest.responses.voting.PageResponse;
import host.galactic.stellar.rest.responses.voting.VotingResponse;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

@RequestScoped
class StellarVotingRestGetVotings {
    @Inject
    VotingRepository votingRepository;

    @Inject
    UserInfo userInfo;

    @WithSession
    public Uni<VotingResponse> byId(Long id) {
        Log.infof("byId(): Got request to get voting by id = %s", id);

        return votingRepository.getById(id)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException())
                .onItem()
                .call(e -> Mutiny.fetch(e.voters))
                .invoke(e -> checkIfUserIsAllowedToGetVoting(e, userInfo.getEmail()))
                .map(VotingEntityMapper::from)
                .onFailure()
                .invoke(t -> Log.warn("byId(): Could not get voting!", t));
    }

    @WithSession
    public Uni<PageResponse<VotingResponse>> getCreated(int page) {
        Log.infof("getCreated(): Got request to get %s's votings as creator at page: %s", userInfo.getEmail(), page);
        return votingRepository.getVotingsOfUser(userInfo.getEmail(), page)
                .map(this::toResponse);
    }

    @WithSession
    public Uni<PageResponse<VotingResponse>> getOfVoter(int page) {
        Log.infof("getOfVoter(): Got request to get %s's votings as voter at page: %s", userInfo.getEmail(), page);
        return votingRepository.getVotingsOfVoter(userInfo.getEmail(), page)
                .map(this::toResponse);
    }

    private void checkIfUserIsAllowedToGetVoting(VotingEntity voting, String email) {
        if (voting.visibility == Visibility.PRIVATE &&
                doesUserNotParticipateIn(voting, email) &&
                isUserNotCreatorOf(voting, email)) {
            Log.warnf("User \"%s\" is not allowed to get voting with id = %s", email, voting.id);
            throw new ForbiddenException();
        }
    }

    private boolean doesUserNotParticipateIn(VotingEntity entity, String email) {
        return entity.voters.stream()
                .map(u -> u.email)
                .noneMatch(e -> e.equals(email));
    }

    private boolean isUserNotCreatorOf(VotingEntity voting, String email) {
        return !voting.createdBy.email.equals(email);
    }

    private PageResponse<VotingResponse> toResponse(Page<VotingEntity> page) {
        var items = page.items().stream()
                .map(VotingEntityMapper::from)
                .toList();

        return new PageResponse<>(items, page.totalPages());
    }
}
