package host.galactic.stellar;

import host.galactic.stellar.rest.StellarCommissionRest;
import host.galactic.stellar.rest.StellarVotingRest;
import host.galactic.testutils.AuthForTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.net.URL;

@QuarkusTest
public class StellarBaseTest {
    @TestHTTPEndpoint(StellarVotingRest.class)
    @TestHTTPResource
    private URL votingRestUrl;

    @TestHTTPEndpoint(StellarCommissionRest.class)
    @TestHTTPResource
    private URL commisionRestUrl;

    @Inject
    private EntityManager entityManager;

    @Inject
    private AuthForTest authForTest;

    public StellarBaseTestRest rest;
    public StellarBaseTestDb db;
    public StellarBaseTestUtils utils;

    @BeforeEach
    void init() {
        var voting = new StellarBaseTestRestVoting(authForTest, votingRestUrl);
        var commission = new StellarBaseTestRestCommission(commisionRestUrl);

        rest = new StellarBaseTestRest(authForTest, voting, commission);
        db = new StellarBaseTestDb(entityManager);
        utils = new StellarBaseTestUtils();
    }
}
