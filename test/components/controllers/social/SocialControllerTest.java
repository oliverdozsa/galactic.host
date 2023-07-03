package components.controllers.social;

import components.clients.social.SocialTestClient;
import components.extractors.social.ActorResponseFromResult;
import controllers.social.routes;
import io.ipfs.api.IPFS;
import ipfs.api.IpfsApi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import rules.RuleChainForTests;
import security.jwtverification.JwtVerification;
import security.jwtverification.JwtVerificationForTests;
import units.ipfs.api.imp.MockIpfsApi;
import units.ipfs.api.imp.MockIpfsProvider;

import static components.extractors.GenericDataFromResult.statusOf;
import static matchers.ResultHasHeader.hasLocationHeader;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static play.inject.Bindings.bind;
import static play.mvc.Http.HeaderNames.LOCATION;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.OK;

public class SocialControllerTest {
    @Rule
    public RuleChain chain;

    private final RuleChainForTests ruleChainForTests;

    private SocialTestClient client;

    public SocialControllerTest() {
        GuiceApplicationBuilder applicationBuilder = new GuiceApplicationBuilder()
                .overrides(bind(IpfsApi.class).to(MockIpfsApi.class))
                .overrides(bind(IPFS.class).toProvider(MockIpfsProvider.class))
                .overrides((bind(JwtVerification.class).qualifiedWith("auth0").to(JwtVerificationForTests.class)));

        ruleChainForTests = new RuleChainForTests(applicationBuilder);
        chain = ruleChainForTests.getRuleChain();
    }

    @Before
    public void setup() {
        client = new SocialTestClient(ruleChainForTests.getApplication());
    }

    @Test
    public void testSignup() {
        // Given
        // When
        Result result = client.signup("alice");

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
    public void testFollowers() {
        // Given
        // When
        // Then
        // TODO
        //  Create two actors, Alice and Bob.
        //  Bob follows Alice.
        //  Check that Bob is present in Alice's followers
    }

    @Test
    public void testFollowing() {
        // Given
        // When
        // Then
        // TODO
        //  Create two actors, Alice and Bob.
        //  Bob follows Alice.
        //  Check that Alice is in Bob's following
    }

    @Test
    public void testLiked() {
        // Given
        // When
        // Then
        // TODO
        //  Create two actors, Alice and Bob.
        //  Bob creates a post.
        //  Alice likes that.
        //  Check that Alice's like collection contains the new like.
    }

    @Test
    public void testInbox() {
        // Given
        // When
        // Then
        // TODO
        //  Create two actors Alice and Bob.
        //  Bobs is authorized to send posts to Alice.
        //  Bob sends a post to Alice.
        //  Check that Alice's inbox contains the new post.
    }

    @Test
    public void testOutbox() {
        // Given
        // When
        // Then
        // TODO
        //  Create an actor Alice.
        //  Alice creates a public post for herself
        //  Check that the new post is in Alice's outbox
    }

    // TODO: tests for unhappy cases
}
