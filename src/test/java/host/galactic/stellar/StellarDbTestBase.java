package host.galactic.stellar;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class StellarDbTestBase {
    @Inject
    public EntityManager entityManager;
}
