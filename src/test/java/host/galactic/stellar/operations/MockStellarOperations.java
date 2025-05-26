package host.galactic.stellar.operations;

public class MockStellarOperations {
    static boolean transferXlmShouldSucceed = true;

    public static void failTransferXlm() {
        transferXlmShouldSucceed = false;
    }

    public static void succeedTransferXlm() {
        transferXlmShouldSucceed = true;
    }
}
