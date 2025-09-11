package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import org.stellar.sdk.*;

import java.util.List;

class StellarOperationsImp implements StellarOperations {
    private static final Server testServer = new Server("https://horizon-testnet.stellar.org");
    private static final Server mainServer = new Server("https://horizon.stellar.org");

    private final Server server;
    private final Network network;

    public StellarOperationsImp(boolean isOnTestNet) {
        server = isOnTestNet ? testServer : mainServer;
        network = isOnTestNet ? Network.TESTNET : Network.PUBLIC;
    }

    public Uni<Void> createInternalFunding(String sourceAccountSecret, double startingXlm, String targetAccountSecret) {
        return new StellarCreateInternalFunding(server, network)
                .create(sourceAccountSecret, startingXlm, targetAccountSecret);
    }

    @Override
    public Uni<List<StellarChannelGenerator>> createChannelGenerators(StellarChannelGeneratorOperationPayload payload) {
        return new StellarChannelGeneratorOperation(server, network)
                .create(payload);
    }
}
