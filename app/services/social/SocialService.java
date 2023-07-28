package services.social;

import controllers.social.routes;
import data.entities.social.JpaActor;
import data.operations.social.ActorDbOperations;
import galactic.blockchain.operations.SocialBlockchainOperations;
import play.Logger;
import play.mvc.Http;
import requests.social.SignupRequest;
import responses.social.ActorResponse;
import security.VerifiedJwt;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class SocialService {
    private final SocialBlockchainOperations socialBlockchainOperations;
    private final ActorDbOperations actorDbOperations;

    private static final Logger.ALogger logger = Logger.of(SocialService.class);

    @Inject
    public SocialService(SocialBlockchainOperations socialBlockchainOperations, ActorDbOperations actorDbOperations) {
        this.socialBlockchainOperations = socialBlockchainOperations;
        this.actorDbOperations = actorDbOperations;

    }

    public CompletionStage<String> signup(SignupRequest signupRequest, VerifiedJwt jwt) {
        logger.info("signup(): signupRequest = {}", signupRequest.toString());

        return socialBlockchainOperations.signup(signupRequest)
                .thenCompose(v -> this.actorDbOperations.createFrom(signupRequest, jwt.getEmail()))
                .thenApply(JpaActor::getUserId);
    }

    public CompletionStage<ActorResponse> getActor(String userId, Http.Request request) {
        logger.info("getActor(): userId = {}", userId);

        return actorDbOperations.getByUserId(userId)
                .thenApply(e -> fromJpaActor(e, request));
    }

    private static ActorResponse fromJpaActor(JpaActor entity, Http.Request request) {
        ActorResponse actorResponse = new ActorResponse();
        actorResponse.setFollowing(routes.SocialController.getFollowingOf(entity.getUserId()).absoluteURL(request));
        actorResponse.setFollowers(routes.SocialController.getFollowersOf(entity.getUserId()).absoluteURL(request));
        actorResponse.setLiked(routes.SocialController.getLikedOf(entity.getUserId()).absoluteURL(request));
        actorResponse.setInbox(routes.SocialController.getInboxOf(entity.getUserId()).absoluteURL(request));
        actorResponse.setOutbox(routes.SocialController.getOutboxOf(entity.getUserId()).absoluteURL(request));

        // TODO
        return null;
    }
}
