package host.galactic.stellar.operations;

import host.galactic.data.entities.VotingEntity;

public record StellarAssetAccountsOperationPayload (String fundingSecret, VotingEntity votingEntity) {
}
