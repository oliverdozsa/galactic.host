package services.social;

import galactic.blockchain.operations.SocialBlockchainOperations;
import play.Logger;
import requests.social.SignupRequest;

import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class SocialService {
    private final SocialBlockchainOperations socialBlockchainOperations;

    private static final Logger.ALogger logger = Logger.of(SocialService.class);

    public SocialService(SocialBlockchainOperations socialBlockchainOperations) {
        this.socialBlockchainOperations = socialBlockchainOperations;
    }

    public CompletionStage<String> signup(SignupRequest signupRequest) {
        logger.info("signup(): signupRequest = {}", signupRequest.toString());



        // TODO
        return null;
    }
}
