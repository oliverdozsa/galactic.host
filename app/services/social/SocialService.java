package services.social;

import data.entities.social.JpaActor;
import data.operations.social.ActorDbOperations;
import galactic.blockchain.operations.SocialBlockchainOperations;
import play.Logger;
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
                .thenApply(v -> v.getUserId());
    }

    public CompletionStage<ActorResponse> getActor(String userId) {
        logger.info("getActor(): userId = {}", userId);

        return actorDbOperations.getByUserId(userId)
                .thenApply(SocialService::fromJpaActor);
    }

    private static ActorResponse fromJpaActor(JpaActor entity) {
        // TODO
        return null;
    }
}
