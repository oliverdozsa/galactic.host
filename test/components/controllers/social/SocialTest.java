package components.controllers.social;

import components.clients.social.SocialTestClient;
import io.ipfs.api.IPFS;
import ipfs.api.IpfsApi;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import play.inject.guice.GuiceApplicationBuilder;
import rules.RuleChainForTests;
import security.jwtverification.JwtVerification;
import security.jwtverification.JwtVerificationForTests;
import units.ipfs.api.imp.MockIpfsApi;
import units.ipfs.api.imp.MockIpfsProvider;

import static play.inject.Bindings.bind;

class SocialTest {
    @Rule
    public RuleChain chain;

    protected final RuleChainForTests ruleChainForTests;

    protected SocialTestClient client;

    public SocialTest() {
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
}
