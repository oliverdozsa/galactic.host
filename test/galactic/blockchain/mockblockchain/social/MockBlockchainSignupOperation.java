package galactic.blockchain.mockblockchain.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.SignupOperation;

import java.util.HashMap;
import java.util.Map;

public class MockBlockchainSignupOperation implements SignupOperation {
    private static boolean isAccountValidValue = true;
    private static boolean hasEnoughBalanceValue = true;
    public static Map<Account, String> profileCids = new HashMap<>();

    @Override
    public void init(BlockchainConfiguration configuration) {

    }

    @Override
    public void useTestNet() {

    }

    @Override
    public boolean isAccountValid(Account account) {
        return isAccountValidValue;
    }

    @Override
    public boolean hasEnoughBalance(Account account) {
        return hasEnoughBalanceValue;
    }

    @Override
    public void setProfileCid(Account account, String cid) {
        profileCids.put(account, cid);
    }

    public static void forceIsAccountValidTo(boolean value) {
        isAccountValidValue = value;
    }

    public static void forceHasEnoughBalanceValueTo(boolean value) {
        hasEnoughBalanceValue = value;
    }
}