package controllers.social;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultExceptionMapper;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import requests.social.SignupRequest;
import responses.social.ActorResponse;
import security.SecurityUtils;
import security.VerifiedJwt;
import services.social.SocialService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Results.*;

public class SocialController {
    private final FormFactory formFactory;
    private final SocialService socialService;

    private static final Logger.ALogger logger = Logger.of(SocialController.class);

    private final Function<Throwable, Result> mapException = new DefaultExceptionMapper(logger);
    private final Function<Throwable, Result> mapExceptionWithUnpack = e -> mapException.apply(e.getCause());


    @Inject
    public SocialController(FormFactory formFactory, SocialService socialService) {
        this.formFactory = formFactory;
        this.socialService = socialService;
    }

    public CompletionStage<Result> signup(Http.Request request) {
        Form<SignupRequest> form = formFactory.form(SignupRequest.class).bindFromRequest(request);

        if (form.hasErrors()) {
            JsonNode errorJson = form.errorsAsJson();
            logger.warn("signup(): request has errors! error json:\n{}", form.errorsAsJson().toPrettyString());
            return completedFuture(badRequest(errorJson));
        }

        VerifiedJwt jwt = SecurityUtils.getFromRequest(request);
        SignupRequest signupRequest = form.get();

        logger.info("signup(): signupRequest = {}", signupRequest.toString());

        return socialService.signup(signupRequest, jwt)
                .thenApply(userId -> signedUpToResult(userId, request))
                .exceptionally(mapExceptionWithUnpack);
    }

    public CompletionStage<Result> getActor(String userId, Http.Request request) {
        logger.info("getActor(): userId = {}", userId);

        return socialService.getActor(userId, request)
                .thenApply(this::toResult)
                .exceptionally(mapExceptionWithUnpack);
    }

    public CompletionStage<Result> getFollowingOf(String userId, Http.Request request) {
        // TODO
        return null;
    }

    public CompletionStage<Result> getFollowersOf(String userId, Http.Request request) {
        // TODO
        return null;
    }

    public CompletionStage<Result> getLikedOf(String userId, Http.Request request) {
        // TODO
        return null;
    }

    public CompletionStage<Result> getInboxOf(String userId, Http.Request request) {
        // TODO
        return null;
    }

    public CompletionStage<Result> getOutboxOf(String userId, Http.Request request) {
        // TODO
        return null;
    }

    public Result signedUpToResult(String userId, Http.Request request) {
        String location = routes.SocialController.getActor(userId).absoluteURL(request);
        return created()
                .withHeader(LOCATION, location);
    }

    public Result toResult(ActorResponse actorResponse) {
        return ok(Json.toJson(actorResponse));
    }
}
