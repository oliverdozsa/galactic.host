package data.operations.social;

import data.entities.social.JpaActor;
import data.repositories.social.ActorRepository;
import executioncontexts.DatabaseExecutionContext;
import play.Logger;
import requests.social.SignupRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class ActorDbOperations {
    private final DatabaseExecutionContext dbExecContext;
    private final ActorRepository actorRepository;

    private static final Logger.ALogger logger = Logger.of(ActorDbOperations.class);

    @Inject
    public ActorDbOperations(DatabaseExecutionContext dbExecContext, ActorRepository actorRepository) {
        this.dbExecContext = dbExecContext;
        this.actorRepository = actorRepository;
    }

    public CompletionStage<JpaActor> createFrom(SignupRequest signupRequest, String userEmail) {
        return supplyAsync(() -> {
            logger.info("createFrom(): signupRequest = {}, userEmail = {}", signupRequest, userEmail);
            return actorRepository.createFrom(signupRequest, userEmail);
        }, dbExecContext);
    }

    public CompletionStage<JpaActor> getByUserId(String userId){
        return supplyAsync(() -> {
            logger.info("getByUserId(): userId = {}", userId);
            return actorRepository.getByUserId(userId);
        }, dbExecContext);
    }
}
