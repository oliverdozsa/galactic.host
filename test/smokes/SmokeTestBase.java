package smokes;

import org.junit.Rule;
import org.junit.rules.RuleChain;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.ws.WSClient;
import rules.RuleChainForTests;
import security.jwtverification.JwtVerification;
import security.jwtverification.JwtVerificationForTests;
import smokes.fixtures.BlockchainTestNet;
import smokes.fixtures.StellarBlockchainTestNet;

import java.util.HashMap;
import java.util.Map;

import static play.inject.Bindings.bind;

public class SmokeTestBase {
    @Rule
    public RuleChain chain;

    protected final RuleChainForTests ruleChainForTests;

    protected static final String[] supportedNetworks = new String[]{
            "stellar"
    };

    protected static Map<String, BlockchainTestNet> testNets = new HashMap<>();

    public SmokeTestBase() {
        GuiceApplicationBuilder applicationBuilder = new GuiceApplicationBuilder()
                .overrides((bind(JwtVerification.class).qualifiedWith("auth0").to(JwtVerificationForTests.class)));

        ruleChainForTests = new RuleChainForTests(applicationBuilder);
        chain = ruleChainForTests.getRuleChain();
    }

    protected void initTestNets() {
        WSClient wsClient = ruleChainForTests.getApplication().injector().instanceOf(WSClient.class);

        StellarBlockchainTestNet stellarTestNet = new StellarBlockchainTestNet(wsClient);
        testNets.put("stellar", stellarTestNet);
    }
}
