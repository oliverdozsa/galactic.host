package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;


@Mock
@ApplicationScoped
public class MockStellarOperationsProducer extends StellarOperationsProducer {
    @Override
    public StellarOperations create(boolean isOnTestNet) {
        Log.info("Using mock stellar operations.");
        return null;
    }
}
