package galactic.blockchain.api.voting;


import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;

public interface DistributionAndBallotAccountOperation extends BlockchainOperation {
    TransactionResult create(Account funding, String assetCode, long votesCap);


    class TransactionResult {
        public final Account distribution;
        public final Account ballot;
        public final Account issuer;

        public TransactionResult(Account distribution, Account ballot, Account issuer) {
            this.distribution = distribution;
            this.ballot = ballot;
            this.issuer = issuer;
        }
    }
}
