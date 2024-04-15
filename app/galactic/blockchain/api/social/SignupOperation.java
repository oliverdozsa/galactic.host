package galactic.blockchain.api.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;

public interface SignupOperation extends BlockchainOperation {
    boolean isAccountValid(Account account);
    boolean hasEnoughBalance(Account account);
    void createProfile(String cid);

}
