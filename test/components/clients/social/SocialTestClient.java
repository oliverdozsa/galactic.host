package components.clients.social;

import components.clients.TestClient;
import controllers.social.routes;
import play.Application;
import play.mvc.Http;
import play.mvc.Result;

import static play.mvc.Http.HeaderNames.CONTENT_TYPE;
import static play.mvc.Http.HttpVerbs.GET;
import static play.mvc.Http.HttpVerbs.POST;
import static play.test.Helpers.route;
import static utils.JwtTestUtils.addJwtTokenTo;

public class SocialTestClient extends TestClient {

    public SocialTestClient(Application application) {
        super(application);
    }

    public Result signup(String userId) {
        Http.RequestBuilder httpRequest = new Http.RequestBuilder()
                .method(POST)
                .header(CONTENT_TYPE, Http.MimeTypes.JSON)
                // TODO: .bodyJson()
                .uri(routes.SocialController.signup().url());

        String jwt = jwtTestUtils.createToken(userId, userId + "@mail.com");
        addJwtTokenTo(httpRequest, jwt);
        return route(application, httpRequest);
    }

    public Result getActorWithJwt(String userId, String userJwt) {
        Http.RequestBuilder httpRequest = new Http.RequestBuilder()
                .method(GET)
                .header(CONTENT_TYPE, Http.MimeTypes.JSON)
                .uri(routes.SocialController.getActor(userId).url());

        String jwt = jwtTestUtils.createToken(userId, userJwt + "@mail.com");
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
}
