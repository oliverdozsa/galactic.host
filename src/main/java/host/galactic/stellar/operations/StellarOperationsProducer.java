package host.galactic.stellar.operations;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StellarOperationsProducer {
    public StellarOperations create(boolean isOnTestNet) {
        return new StellarOperations(isOnTestNet);
    }
}
