package host.galactic.stellar.operations;

import java.util.List;

public record StellarChannelGenerators(Long votingId, List<StellarChannelGenerator> generatorAccounts) {
}
