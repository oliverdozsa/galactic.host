package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;

import java.util.List;

class MockStellarOperationsImp implements StellarOperations {

    @Override
    public Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret) {
        if (MockStellarOperations.transferXlmShouldSucceed) {
            return Uni.createFrom().voidItem();
        } else {
            var exception = new StellarOperationsException("Mock failure has been thrown to test failure of transferXlmFrom()!");
            return Uni.createFrom().failure(exception);
        }
    }

    @Override
    public Uni<List<StellarChannelGenerator>> createChannelGenerators(String fundingAccountSecret, int maxVoters) {
        // TODO
        return null;
    }
}
