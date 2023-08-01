package galactic.blockchain.api.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;
import play.libs.ws.WSClient;

public interface CostAccountOperation extends BlockchainOperation {
    Account getAccount();
}
