package galactic.blockchain.api.voting;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainOperation;

import java.util.List;

public interface ChannelAccountOperation extends BlockchainOperation {
    int maxNumOfAccountsToCreateInOneBatch();

    List<Account> create(ChannelGenerator channelGenerator, int numOfAccountsToCreate);
}
