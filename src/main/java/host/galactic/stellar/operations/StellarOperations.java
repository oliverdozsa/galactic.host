package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;

public class StellarOperations {
    private final String url;

    public StellarOperations(boolean isOnTestNet) {
        if(isOnTestNet) {
            url = "https://horizon-testnet.stellar.org";
        } else {
            url = "https://horizon.stellar.org";
        }
    }

    public Uni<String> deductEstimatedCostFrom(String fundingAccountSecret, int numOfVoters) {
        // TODO
        return null;
    }
}
