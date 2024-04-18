package components.controllers.social;

import components.extractors.social.ActorResponseFromResult;
import controllers.social.routes;
import galactic.blockchain.mockblockchain.social.MockBlockchainSocialOperation;
import org.junit.After;
import org.junit.Test;
import play.mvc.Result;
import requests.social.SignupRequest;

import static components.extractors.GenericDataFromResult.statusOf;
import static matchers.ResultHasHeader.hasLocationHeader;
import static org.hamcrest.CoreMatchers.containsString;
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
        signupRequest.setUserId("alice");
        signupRequest.setNetwork("mockblockchain");
        signupRequest.setAccountPublic("mockpublic");
        signupRequest.setAccountSecret("mocksecret");
        signupRequest.setName("Actor Alice");
        signupRequest.setPreferredUserName("Alice");

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
        assertThat(id, containsString(routes.SocialController.getActor("alice").url()));

        String following = ActorResponseFromResult.followingOf(getByLocationResult);
        assertThat(following, containsString(routes.SocialController.getActor("alice").url() + "/following"));

        String followers = ActorResponseFromResult.followersOf(getByLocationResult);
        assertThat(followers, containsString(routes.SocialController.getActor("alice").url() + "/followers"));

        String liked = ActorResponseFromResult.likedOf(getByLocationResult);
        assertThat(liked, containsString(routes.SocialController.getActor("alice").url() + "/liked"));

        String inbox = ActorResponseFromResult.inboxOf(getByLocationResult);
        assertThat(inbox, containsString(routes.SocialController.getActor("alice").url() + "/inbox"));

        String outbox = ActorResponseFromResult.outboxOf(getByLocationResult);
        assertThat(outbox, containsString(routes.SocialController.getActor("alice").url() + "/outbox"));

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
        signupRequest.setUserId("alice");
        signupRequest.setNetwork("some-unknown-network");
        signupRequest.setAccountPublic("mockpublic");
        signupRequest.setAccountSecret("mocksecret");

        Result result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @Test
    public void testSignup_NotEnoughBalance() {
        // Given
        MockBlockchainSocialOperation.forceHasEnoughBalanceValueTo(false);

        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId("alice");
        signupRequest.setNetwork("mockblockchain");
        signupRequest.setAccountPublic("mockpublic-low-balance");
        signupRequest.setAccountSecret("mocksecret-low-balance");

        Result result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @Test
    public void testSignup_AccountInvalid() {
        // Given
        MockBlockchainSocialOperation.forceIsAccountValidTo(false);

        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId("alice");
        signupRequest.setNetwork("mockblockchain");
        signupRequest.setAccountPublic("mockpublic-invalid");
        signupRequest.setAccountSecret("mocksecret-invalid");

        Result result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @Test
    public void testSignup_AlreadySignedUp() {
        // Given
        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId("alice");
        signupRequest.setNetwork("mockblockchain");
        signupRequest.setAccountPublic("mockpublic");
        signupRequest.setAccountSecret("mocksecret");

        Result result = client.signup("alice", signupRequest);
        assertThat(statusOf(result), equalTo(CREATED));

        result = client.signup("alice", signupRequest);

        // Then
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @Test
    public void testSignup_UserIdAlreadyExists() {
        // Given
        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId("alice");
        signupRequest.setNetwork("mockblockchain");
        signupRequest.setAccountPublic("mockpublic");
        signupRequest.setAccountSecret("mocksecret");

        Result result = client.signup("alice", signupRequest);
        assertThat(statusOf(result), equalTo(CREATED));

        SignupRequest signupRequestWithUserIdAlreadyExist = new SignupRequest();
        signupRequestWithUserIdAlreadyExist.setUserId("alice");
        signupRequestWithUserIdAlreadyExist.setNetwork("mockblockchain");
        signupRequestWithUserIdAlreadyExist.setAccountPublic("mockpublic");
        signupRequestWithUserIdAlreadyExist.setAccountSecret("mocksecret");

        // Then
        result = client.signup("bob", signupRequestWithUserIdAlreadyExist);
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @Test
    public void testSignup_InvalidUserId() {
        // Given
        // When
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUserId("a!ice");
        signupRequest.setNetwork("mockblockchain");
        signupRequest.setAccountPublic("mockpublic");
        signupRequest.setAccountSecret("mocksecret");

        Result result = client.signup("alice", signupRequest);
        assertThat(statusOf(result), equalTo(BAD_REQUEST));
    }

    @After
    public void tearDown(){
        MockBlockchainSocialOperation.forceIsAccountValidTo(true);
        MockBlockchainSocialOperation.forceHasEnoughBalanceValueTo(true);
    }
}
