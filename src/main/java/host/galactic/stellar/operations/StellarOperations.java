package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;

import java.util.List;

public interface StellarOperations {
    Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret);
    Uni<List<StellarChannelGenerator>> createChannelGenerators(String fundingAccountSecret, int maxVoters, Long votingId);
}
