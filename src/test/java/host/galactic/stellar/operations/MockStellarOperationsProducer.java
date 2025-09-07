package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;


@Mock
@ApplicationScoped
public class MockStellarOperationsProducer extends StellarOperationsProducer {
    @ConfigProperty(name = "galactic.host.vote.buckets")
    private Integer voteBuckets;

    @Override
    public StellarOperations create(boolean isOnTestNet) {
        Log.info("Using mock stellar operations.");
        return new MockStellarOperationsImp(voteBuckets);
    }
}
