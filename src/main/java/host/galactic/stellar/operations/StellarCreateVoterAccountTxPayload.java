package host.galactic.stellar.operations;

public record StellarCreateVoterAccountTxPayload(
        String channelAccountSecret,
        String distributionAccountSecret,
        String issuerAccountPublic,
        String assetCode,
        long votesCap,
        String voterAccountPublic
) {
}
