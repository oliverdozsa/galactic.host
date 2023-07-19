package galactic.blockchain.api.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;

public interface CostAccountOperation extends BlockchainOperation {
    void createOnTestnetIfNotExists(Account account);
}
