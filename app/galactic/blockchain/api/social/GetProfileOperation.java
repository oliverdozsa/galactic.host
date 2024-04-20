package galactic.blockchain.api.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;

public interface GetProfileOperation extends BlockchainOperation {
    String getProfileCid(Account account);
}
