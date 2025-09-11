package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;

import java.util.List;

public interface StellarOperations {
    Uni<Void> createInternalFunding(String sourceAccountSecret, double startingXlm, String targetAccountSecret);
    Uni<List<StellarChannelGenerator>> createChannelGenerators(StellarChannelGeneratorOperationPayload payload);
}
