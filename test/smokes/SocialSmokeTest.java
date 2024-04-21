package smokes;

import components.clients.social.SocialTestClient;
import components.extractors.social.ActorResponseFromResult;
import controllers.social.routes;
import galactic.blockchain.api.Account;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import requests.social.SignupRequest;

import static components.extractors.GenericDataFromResult.statusOf;
import static matchers.ResultHasHeader.hasLocationHeader;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;


public class SocialSmokeTest extends SmokeTestBase {
    private SocialTestClient testClient;

    @Before
    public void setup() {
        testClient = new SocialTestClient(ruleChainForTests.getApplication());
        initTestNets();
    }

    @Test
    public void testSignupOnNetworks() {
        for (String network : supportedNetworks) {
            testSignupOnNetwork(network);
        }
    }

    private void testSignupOnNetwork(String network) {
        String user = "Alice";
        SignupRequest signupRequest = createSignupRequestFor(network, user);
        Result result = testClient.signup(user, signupRequest);

        assertThat(statusOf(result), equalTo(CREATED));

        assertThat(statusOf(result), equalTo(CREATED));
        assertThat(result, hasLocationHeader());

        String locationUrl = result.headers().get(LOCATION);

        Result getByLocationResult = testClient.byLocation(locationUrl);

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
        assertThat(name, equalTo("Alice"));

        String type = ActorResponseFromResult.typeOf(getByLocationResult);
        assertThat(type, equalTo("Person"));

        String preferredUserName = ActorResponseFromResult.preferredUsernameOf(getByLocationResult);
        assertThat(preferredUserName, equalTo("Alice Galactic"));
    }

    private SignupRequest createSignupRequestFor(String network, String user) {
        SignupRequest request = new SignupRequest();
        request.setName(user);
        request.setPreferredUserName(user + " Galactic");
        request.setNetwork(network);
        request.setUserId(user.toLowerCase());
        request.setUseTestnet(true);

        Account account = generateAccountFor(network);
        request.setAccountPublic(account.publik);
        request.setAccountSecret(account.secret);

        return request;
    }

    private Account generateAccountFor(String network) {
        return testNets.get(network).createAccountWithBalance(10000);
    }
}
