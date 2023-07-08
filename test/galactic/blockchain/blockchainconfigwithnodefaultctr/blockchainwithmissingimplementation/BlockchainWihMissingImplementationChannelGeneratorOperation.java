package galactic.blockchain.blockchainconfigwithnodefaultctr.blockchainwithmissingimplementation;

import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.voting.ChannelGenerator;
import galactic.blockchain.api.voting.ChannelGeneratorAccountOperation;
import galactic.blockchain.api.Account;

import java.util.List;

public class BlockchainWihMissingImplementationChannelGeneratorOperation implements ChannelGeneratorAccountOperation {
    @Override
    public void init(BlockchainConfiguration configuration) {

    }

    @Override
    public void useTestNet() {

    }

    @Override
    public List<ChannelGenerator> create(long totalVotesCap, Account funding) {
        return null;
    }

    @Override
    public long calcNumOfAccountsNeeded(long totalVotesCap) {
        return 0L;
    }
}
