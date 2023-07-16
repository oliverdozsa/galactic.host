package controllers.social;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultExceptionMapper;
import play.Logger;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import requests.social.SignupRequest;
import services.social.SocialService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.NOT_IMPLEMENTED;
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

        SignupRequest signupRequest = form.get();
        logger.info("signup(): signupRequest = {}", signupRequest.toString());

        if (form.hasErrors()) {
            JsonNode errorJson = form.errorsAsJson();
            logger.warn("signup(): request has errors! error json:\n{}", form.errorsAsJson().toPrettyString());
            return completedFuture(badRequest(errorJson));
        }

        return socialService.signup(signupRequest)
                .thenApply(userId -> signedUpToResult(userId, request))
                .exceptionally(mapExceptionWithUnpack);
    }

    public CompletionStage<Result> getActor(String id, Http.Request request) {
        logger.info("getActor(): id = {}", id);
        // TODO
        return supplyAsync(() -> status(NOT_IMPLEMENTED));
    }

    public Result signedUpToResult(String userId, Http.Request request) {
        String location = routes.SocialController.getActor(userId).absoluteURL(request);
        return created()
                .withHeader(LOCATION, location);
    }
}
