package galactic.blockchain.api.voting;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;

public interface FundingAccountOperation extends BlockchainOperation {
    boolean doesNotHaveEnoughBalanceForVotesCap(String accountPublic, long votesCap);
    Account createAndFundInternalFrom(Account userGivenFunding, long votesCap);
}
