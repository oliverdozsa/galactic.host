package host.galactic.stellar.operations;

public record StellarAssetAccounts(String distributionAccountSecret, String ballotAccountSecret, String issuerAccountSecret, Long votingId) {
}
