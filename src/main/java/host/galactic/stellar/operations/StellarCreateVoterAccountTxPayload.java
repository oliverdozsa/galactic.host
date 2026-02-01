package host.galactic.stellar.operations;

public record StellarCreateVoterAccountTxPayload(
        String channelAccountSecret,
        String distributionAccountSecret,
        String issuerAccountSecret,
        String assetCode,
        long maxVoters,
        String voterAccountPublic
) {
}
