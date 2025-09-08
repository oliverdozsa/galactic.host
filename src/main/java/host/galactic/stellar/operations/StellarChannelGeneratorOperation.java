package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StellarChannelGeneratorOperation {
    private Server server;
    private Network network;

    public StellarChannelGeneratorOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<List<StellarChannelGenerator>> create(StellarChannelGeneratorOperationPayload payload) {
        return Uni.createFrom().item(() -> {
            int numOfAccountsPerChannelGenerator = payload.maxVoters() / payload.numOfGeneratorsToCreate();
            int numOfAccountPerChannelGeneratorRemainder = payload.maxVoters() % payload.numOfGeneratorsToCreate();

            List<StellarChannelGenerator> generators = new ArrayList<>();

            // TODO

            return generators;
        })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .emitOn(MutinyHelper.executor(Vertx.currentContext()));


    }
}
