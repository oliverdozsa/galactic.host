package galactic.blockchain.mockblockchain.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.GetProfileOperation;

public class MockBlockchainGetProfileOperation implements GetProfileOperation {
    @Override
    public void init(BlockchainConfiguration configuration) {

    }

    @Override
    public void useTestNet() {

    }

    @Override
    public String getProfileCid(Account account) {
        return MockBlockchainSignupOperation.profileCids.get(account);
    }
}
