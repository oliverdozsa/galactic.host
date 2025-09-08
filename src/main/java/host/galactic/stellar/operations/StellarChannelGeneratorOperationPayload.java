package host.galactic.stellar.operations;

public record StellarChannelGeneratorOperationPayload(String fundingAccountSecret, int maxVoters, Long votingId, Integer numOfGeneratorsToCreate) {
}
