package controllers.voting;

import controllers.DefaultExceptionMapper;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import responses.voting.TokenAuthResponse;
import services.voting.TokenAuthService;
import utils.StringUtils;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class TokenAuthController extends Controller {
    private static final Logger.ALogger logger = Logger.of(TokenAuthController.class);

    private final Function<Throwable, Result> mapException = new DefaultExceptionMapper(logger);
    private final Function<Throwable, Result> mapExceptionWithUnpack = e -> mapException.apply(e.getCause());

    private final TokenAuthService service;

    @Inject
    public TokenAuthController(TokenAuthService service) {
        this.service = service;
    }

    public CompletionStage<Result> auth(String token) {
        logger.info("auth(): token = {}", StringUtils.redactWithEllipsis(token, 5));

        return service.auth(token)
                .thenApply(TokenAuthController::toResult)
                .exceptionally(mapExceptionWithUnpack);
    }

    private static Result toResult(TokenAuthResponse response) {
        return ok(Json.toJson(response));
    }
}
