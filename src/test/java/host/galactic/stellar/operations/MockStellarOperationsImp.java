package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;

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
}
