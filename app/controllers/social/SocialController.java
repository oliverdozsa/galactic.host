package controllers.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;
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
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Results.*;

public class SocialController {
    private final FormFactory formFactory;
    private final SocialService socialService;
    private final JsonSchema activityValidator;

    private static final Logger.ALogger logger = Logger.of(SocialController.class);

    private final Function<Throwable, Result> mapException = new DefaultExceptionMapper(logger);
    private final Function<Throwable, Result> mapExceptionWithUnpack = e -> mapException.apply(e.getCause());


    @Inject
    public SocialController(FormFactory formFactory, SocialService socialService, @Named("activity") JsonSchema activityValidator) {
        this.formFactory = formFactory;
        this.socialService = socialService;
        this.activityValidator = activityValidator;
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

    public CompletionStage<Result> getObject(String objectId, Http.Request request) {
        // TODO
        return null;
    }

    public CompletionStage<Result> postActivity(String userId, Http.Request request) {
        JsonNode activityJson = request.body().asJson();

        if(activityJson == null) {
            logger.warn("postActivity(): non-json object is posted.");
            return completedFuture(badRequest("Non-json object is posted."));
        }

        JsonNode typeJson = activityJson.get("type");
        if(typeJson == null || !typeJson.asText().equals("Create")) {
            logger.info("Wrapping object in Create activity.");
            activityJson = wrapInCreateActivity(activityJson);
        }


        Set<ValidationMessage> validationMessages = activityValidator.validate(activityJson);
        if(!validationMessages.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            validationMessages.forEach(m -> errors.put(m.getMessageKey(), m.getMessage()));

            logger.warn("postActivity(): invalid activity. errors = {}", errors);

            return completedFuture(badRequest(Json.toJson(errors)));
        }

        // TODO
        return null;
    }

    private Result signedUpToResult(String userId, Http.Request request) {
        String location = routes.SocialController.getActor(userId).absoluteURL(request);
        return created()
                .withHeader(LOCATION, location);
    }

    private Result toResult(ActorResponse actorResponse) {
        return ok(Json.toJson(actorResponse));
    }

    private JsonNode wrapInCreateActivity(JsonNode objectToCreate) {
        String createActivityJsonStr = "{\n" +
                "  \"@context\": \"https://www.w3.org/ns/activitystreams\",\n" +
                "  \"type\": \"Create\",\n" +
                "  \"id\": \"https://example.net/~mallory/87374\"\n" +
                "}";
        ObjectNode createActivityJson = (ObjectNode) Json.parse(createActivityJsonStr);
        createActivityJson.set("object", objectToCreate);
        return createActivityJson;
    }
}
