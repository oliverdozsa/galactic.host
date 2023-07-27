package galactic.blockchain.mockblockchain.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.CostAccountOperation;
import org.checkerframework.checker.units.qual.A;

public class MockBlockchainCostAccountOperation implements CostAccountOperation {
    @Override
    public void init(BlockchainConfiguration configuration) {

    }

    @Override
    public void useTestNet() {

    }

    @Override
    public Account getAccount() {
        return new Account("mock-cost-account-secret", "mock-cost-account-public");
    }
}
