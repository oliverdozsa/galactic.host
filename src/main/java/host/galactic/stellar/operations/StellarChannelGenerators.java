package host.galactic.stellar.operations;

import host.galactic.data.entities.VotingEntity;

import java.util.List;

public record StellarChannelGenerators(VotingEntity votingEntity, List<StellarChannelGenerator> generatorAccounts) {
}
