package services.social;

import data.entities.social.JpaActor;
import data.operations.social.ActorDbOperations;
import data.repositories.social.ActorRepository;
import galactic.blockchain.operations.SocialBlockchainOperations;
import play.Logger;
import requests.social.SignupRequest;
import security.VerifiedJwt;
import services.Base62Conversions;

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
                .thenApply(Base62Conversions::encode);
    }
}
