package host.galactic.stellar.operations;

public record StellarChannelAccountOperationPayload(
        String generatorAccountSecret,
        int numOfAccountsToCreate,
        Long votingId
) {
}
