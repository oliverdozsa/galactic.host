package galactic.blockchain.mockblockchain.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.SocialOperation;

public class MockBlockchainSocialOperation implements SocialOperation {
    private static boolean isAccountValidValue = true;
    private static boolean hasEnoughBalanceValue = true;
    private String profileCid;

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
        this.profileCid = cid;
    }

    @Override
    public String getProfileCid(Account account) {
        return profileCid;
    }

    public static void forceIsAccountValidTo(boolean value) {
        isAccountValidValue = value;
    }

    public static void forceHasEnoughBalanceValueTo(boolean value) {
        hasEnoughBalanceValue = value;
    }
}
