package host.galactic.stellar;

import host.galactic.stellar.rest.StellarCommissionRest;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.URL;

@ApplicationScoped
public class StellarRestCommissionTestBase {
    @TestHTTPEndpoint(StellarCommissionRest.class)
    @TestHTTPResource
    public URL url;
}
