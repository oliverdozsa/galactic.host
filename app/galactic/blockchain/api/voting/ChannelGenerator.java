package galactic.blockchain.api.voting;

import galactic.blockchain.api.Account;

public class ChannelGenerator {
    public final Account account;
    public final long votesCap;

    public ChannelGenerator(Account account, long votesCap) {
        this.account = account;
        this.votesCap = votesCap;
    }
}
