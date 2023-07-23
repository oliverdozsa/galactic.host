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

    public CompletionStage<Long> createFrom(SignupRequest signupRequest, String userId) {
        return supplyAsync(() -> {
            logger.info("createFrom(): signupRequest = {}, userId = {}", signupRequest, userId);
            JpaActor newActor = actorRepository.createFrom(signupRequest, userId);
            return newActor.getId();
        }, dbExecContext);
    }
}
