package host.galactic.stellar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StellarTest {
    @Inject
    public StellarRestTestBase rest;

    @Inject
    public StellarDbTestBase db;
}
