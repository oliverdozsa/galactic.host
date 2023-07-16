package components.controllers.social;

import components.extractors.social.ActorResponseFromResult;
import controllers.social.routes;
import org.junit.Test;
import play.mvc.Result;
import requests.social.SignupRequest;

import static components.extractors.GenericDataFromResult.statusOf;
import static matchers.ResultHasHeader.hasLocationHeader;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.*;

public class TestSignup extends SocialTest {
    @Test
    public void testSignup() {
        // Given
        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.network = "mockblockchain";
        signupRequest.accountPublic = "mockpublic";
        signupRequest.accountSecret = "mocksecret";

        Result result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(CREATED));
        assertThat(result, hasLocationHeader());

        String locationUrl = result.headers().get(LOCATION);

        Result getByLocationResult = client.byLocation(locationUrl);

        assertThat(statusOf(getByLocationResult), equalTo(OK));

        String context = ActorResponseFromResult.contextOf(getByLocationResult);
        assertThat(context, equalTo("https://www.w3.org/ns/activitystreams"));

        String id = ActorResponseFromResult.idOf(getByLocationResult);
        assertThat(id, equalTo(routes.SocialController.getActor("alice").url()));

        String following = ActorResponseFromResult.followingOf(getByLocationResult);
        assertThat(following, equalTo(routes.SocialController.getActor("alice").url() + "/following"));

        String followers = ActorResponseFromResult.followersOf(getByLocationResult);
        assertThat(followers, equalTo(routes.SocialController.getActor("alice").url() + "/followers"));

        String liked = ActorResponseFromResult.likedOf(getByLocationResult);
        assertThat(liked, equalTo(routes.SocialController.getActor("alice").url() + "/liked"));

        String inbox = ActorResponseFromResult.inboxOf(getByLocationResult);
        assertThat(inbox, equalTo(routes.SocialController.getActor("alice").url() + "/inbox"));

        String outbox = ActorResponseFromResult.outboxOf(getByLocationResult);
        assertThat(outbox, equalTo(routes.SocialController.getActor("alice").url() + "/outbox"));

        String name = ActorResponseFromResult.nameOf(getByLocationResult);
        assertThat(name, equalTo("Actor Alice"));

        String type = ActorResponseFromResult.typeOf(getByLocationResult);
        assertThat(type, equalTo("Person"));

        String preferredUserName = ActorResponseFromResult.preferredUsernameOf(getByLocationResult);
        assertThat(preferredUserName, equalTo("Alice"));
    }

    @Test
    public void testSignup_InvalidNetwork() {
        // Given
        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.network = "some-unknown-network";
        signupRequest.accountPublic = "mockpublic";
        signupRequest.accountSecret = "mocksecret";

        Result result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @Test
    public void testSignup_NotEnoughBalance() {
        // Given
        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.network = "mockblockchain";
        signupRequest.accountPublic = "mockpublic-low-balance";
        signupRequest.accountSecret = "mocksecret-low-balance";

        Result result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @Test
    public void testSignup_AccountInvalid() {
        // Given
        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.network = "mockblockchain";
        signupRequest.accountPublic = "mockpublic-invalid";
        signupRequest.accountSecret = "mocksecret-invalid";

        Result result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }
}
