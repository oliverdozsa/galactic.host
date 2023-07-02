package controllers.social;

import controllers.DefaultExceptionMapper;
import play.Logger;
import play.data.FormFactory;
import play.mvc.Http;
import play.mvc.Result;
import services.social.SocialService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static play.mvc.Http.Status.NOT_IMPLEMENTED;
import static play.mvc.Results.status;

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
        logger.info("signup()");
        // TODO
        return supplyAsync(() -> status(NOT_IMPLEMENTED));
    }

    public CompletionStage<Result> getActor(String id, Http.Request request) {
        logger.info("getActor(): id = {}", id);
        // TODO
        return supplyAsync(() -> status(NOT_IMPLEMENTED));
    }
}
