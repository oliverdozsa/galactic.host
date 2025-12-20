package host.galactic.stellar;

import jakarta.persistence.EntityManager;

public class StellarBaseTestDb {
    public EntityManager entityManager;

    public StellarBaseTestDb(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
