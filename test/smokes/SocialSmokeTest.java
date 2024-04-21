package smokes;

import components.clients.social.SocialTestClient;
import galactic.blockchain.api.Account;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Result;
import requests.social.SignupRequest;

import static components.extractors.GenericDataFromResult.statusOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static play.mvc.Http.Status.CREATED;


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

        // TODO
    }

    private SignupRequest createSignupRequestFor(String network, String user) {
        SignupRequest request = new SignupRequest();
        request.setName(user);
        request.setPreferredUserName(user + " Galactic");
        request.setNetwork(network);
        request.setUserId(user);
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
