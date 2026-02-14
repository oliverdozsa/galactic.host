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

import static host.galactic.stellar.rest.VotingChecks.doesUserNotParticipateIn;
import static host.galactic.stellar.rest.VotingChecks.doesUserParticipateIn;

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
                .invoke(this::checkIfUserIsAllowedToGetVoting)
                .map(e -> VotingEntityMapper.from(e, doesUserParticipateIn(e, userInfo.getEmail())))
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

    private void checkIfUserIsAllowedToGetVoting(VotingEntity voting) {
        if (voting.visibility == Visibility.PRIVATE &&
                doesUserNotParticipateIn(voting, userInfo.getEmail()) &&
                isUserNotCreatorOf(voting)) {
            Log.warnf("User \"%s\" is not allowed to get voting with id = %s", userInfo.getEmail(), voting.id);
            throw new ForbiddenException();
        }
    }

    private boolean isUserNotCreatorOf(VotingEntity voting) {
        return !voting.createdBy.email.equals(userInfo.getEmail());
    }

    private PageResponse<VotingResponse> toResponse(Page<VotingEntity> page) {
        var items = page.items().stream()
                .map(e -> VotingEntityMapper.from(e, doesUserParticipateIn(e, userInfo.getEmail())))
                .toList();

        return new PageResponse<>(items, page.totalPages());
    }
}
