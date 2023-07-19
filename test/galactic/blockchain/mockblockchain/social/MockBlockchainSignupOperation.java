package galactic.blockchain.mockblockchain.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.SignupOperation;

// TODO
public class MockBlockchainSignupOperation implements SignupOperation {
    @Override
    public void init(BlockchainConfiguration configuration) {

    }

    @Override
    public void useTestNet() {

    }

    @Override
    public boolean isAccountValid(Account account) {
        return false;
    }

    @Override
    public boolean hasEnoughBalance(Account account) {
        return false;
    }

    @Override
    public void deductSignupCost(Account source, Account destination) {

    }
}
