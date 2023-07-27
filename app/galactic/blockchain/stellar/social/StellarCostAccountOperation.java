package galactic.blockchain.stellar.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.CostAccountOperation;
import galactic.blockchain.stellar.StellarBlockchainConfiguration;
import galactic.blockchain.stellar.StellarServerAndNetwork;

public class StellarCostAccountOperation implements CostAccountOperation {
    private StellarServerAndNetwork serverAndNetwork;
    private StellarBlockchainConfiguration configuration;

    private boolean useTestnet;

    @Override
    public void init(BlockchainConfiguration configuration) {
        this.configuration = (StellarBlockchainConfiguration) configuration;
        serverAndNetwork = StellarServerAndNetwork.create(this.configuration);
    }

    @Override
    public void useTestNet() {
        serverAndNetwork = StellarServerAndNetwork.createForTestNet(this.configuration);
        useTestnet = true;
    }

    @Override
    public Account getAccount() {
        Account account = configuration.getSocialCostAccountOf();

        // TODO: create if not exists on testnet

        return account;
    }
}
