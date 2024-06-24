package components.clients.social;

import components.clients.TestClient;
import controllers.social.routes;
import play.Application;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import requests.social.SignupRequest;

import static play.mvc.Http.HeaderNames.CONTENT_TYPE;
import static play.mvc.Http.HttpVerbs.GET;
import static play.mvc.Http.HttpVerbs.POST;
import static play.test.Helpers.route;
import static utils.JwtTestUtils.addJwtTokenTo;

public class SocialTestClient extends TestClient {

    public SocialTestClient(Application application) {
        super(application);
    }

    public Result signup(String userId, SignupRequest request) {
        Http.RequestBuilder httpRequest = new Http.RequestBuilder()
                .method(POST)
                .header(CONTENT_TYPE, Http.MimeTypes.JSON)
                .bodyJson(Json.toJson(request))
                .uri(routes.SocialController.signup().url());

        String jwt = jwtTestUtils.createToken(userId, userId + "@mail.com");
        addJwtTokenTo(httpRequest, jwt);
        return route(application, httpRequest);
    }

    public Result getActorWithJwt(String userId) {
        Http.RequestBuilder httpRequest = new Http.RequestBuilder()
                .method(GET)
                .header(CONTENT_TYPE, Http.MimeTypes.JSON)
                .uri(routes.SocialController.getActor(userId).url());

        String jwt = jwtTestUtils.createToken(userId, userId + "@mail.com");
        addJwtTokenTo(httpRequest, jwt);

        return route(application, httpRequest);
    }

    public Result getActor(String userId) {
        Http.RequestBuilder httpRequest = new Http.RequestBuilder()
                .method(GET)
                .header(CONTENT_TYPE, Http.MimeTypes.JSON)
                .uri(routes.SocialController.getActor(userId).url());

        return route(application, httpRequest);
    }

    public Result byLocationWithUserId(String url, String userId) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri(url);

        String jwt = jwtTestUtils.createToken(userId, userId + "@mail.com");
        addJwtTokenTo(request, jwt);

        return route(application, request);
    }

    public Result postActivity(String userId, String content) {
        // TODO
        return null;
    }

    public Result updateActivity(String userId, String content) {
        // TODO
        return null;
    }

    public Result deleteActivity(String userId, String objectId) {
        // TODO
        return null;
    }

    public Result getOutboxOf(String userId) {
        // TODO
        return null;
    }
}
