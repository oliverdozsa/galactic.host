package galactic.blockchain.stellar.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.CostAccountOperation;
import galactic.blockchain.stellar.StellarBlockchainConfiguration;
import galactic.blockchain.stellar.StellarServerAndNetwork;

public class StellarCostAccountOperation implements CostAccountOperation {
    private StellarServerAndNetwork serverAndNetwork;
    private StellarBlockchainConfiguration configuration;

    @Override
    public void init(BlockchainConfiguration configuration) {
        // TODO
    }

    @Override
    public void useTestNet() {
        // TODO
    }

    @Override
    public void createOnTestnetIfNotExists(Account account) {
        // TODO
    }
}
