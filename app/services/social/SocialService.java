package services.social;

import data.entities.social.JpaActor;
import data.repositories.social.ActorRepository;
import galactic.blockchain.operations.SocialBlockchainOperations;
import play.Logger;
import requests.social.SignupRequest;
import security.VerifiedJwt;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class SocialService {
    private final SocialBlockchainOperations socialBlockchainOperations;
    private final ActorRepository actorRepository;

    private static final Logger.ALogger logger = Logger.of(SocialService.class);

    @Inject
    public SocialService(SocialBlockchainOperations socialBlockchainOperations, ActorRepository actorRepository) {
        this.socialBlockchainOperations = socialBlockchainOperations;
        this.actorRepository = actorRepository;
    }

    public CompletionStage<String> signup(SignupRequest signupRequest, VerifiedJwt jwt) {
        logger.info("signup(): signupRequest = {}", signupRequest.toString());

        return socialBlockchainOperations.signup(signupRequest)
                .thenApply(r -> this.actorRepository.createFrom(signupRequest, jwt.getEmail()))
                .thenApply(SocialService::getId);
    }

    private static String getId(JpaActor actor) {
        return "";
    }
}
