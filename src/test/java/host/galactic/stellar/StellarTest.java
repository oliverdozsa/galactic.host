package host.galactic.stellar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StellarTest {
    @Inject
    private StellarRestTestBase rest;

    @Inject
    private StellarDbTestBase db;

    public StellarRestTestBase getRest() {
        return rest;
    }

    public StellarDbTestBase getDb() {
        return db;
    }
}
