package host.galactic.stellar;

import host.galactic.stellar.rest.StellarCommissionRest;
import host.galactic.stellar.rest.StellarVotingRest;
import host.galactic.testutils.AuthForTest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.net.URL;

@ApplicationScoped
public class StellarTestBase {
    @Inject
    public StellarRestTestBase rest;

    @Inject
    public StellarDbTestBase db;
}
