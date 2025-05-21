package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;

public class MockStellarOperationsImp implements StellarOperations {
    private static boolean transferXlmShouldSucceed = true;

    @Override
    public Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret) {
        // TODO: use flag
        return Uni.createFrom().voidItem();
    }

    public static void failTransferXlm() {
        transferXlmShouldSucceed = false;
    }

    public static void succeedTransferXlm() {
        transferXlmShouldSucceed = true;
    }
}
