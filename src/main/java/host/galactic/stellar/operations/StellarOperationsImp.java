package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.PaymentOperation;

import java.math.BigDecimal;
import java.util.List;

import static host.galactic.stellar.operations.StellarUtils.toAccountId;
import static host.galactic.stellar.operations.StellarUtils.toTruncatedAccountId;
import static org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE;

class StellarOperationsImp implements StellarOperations {
    private static final Server testServer = new Server("https://horizon-testnet.stellar.org");
    private static final Server mainServer = new Server("https://horizon.stellar.org");

    private final Server server;
    private final Network network;

    public StellarOperationsImp(boolean isOnTestNet) {
        server = isOnTestNet ? testServer : mainServer;
        network = isOnTestNet ? Network.TESTNET : Network.PUBLIC;
    }

    public Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret) {
        return new StellarTransferXlmOperation(server, network)
                .transfer(sourceAccountSecret, xlm, targetAccountSecret);
    }

    @Override
    public Uni<List<StellarChannelGenerator>> createChannelGenerators(String fundingAccountSecret, int maxVoters, Long votingId) {
        // TODO
        return null;
    }
}
