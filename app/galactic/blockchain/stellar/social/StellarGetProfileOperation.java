package galactic.blockchain.stellar.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.GetProfileOperation;
import galactic.blockchain.stellar.StellarBlockchainConfiguration;
import galactic.blockchain.stellar.StellarServerAndNetwork;
import galactic.blockchain.stellar.StellarUtils;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import play.Logger;

import java.io.IOException;
import java.math.BigDecimal;

import static galactic.blockchain.stellar.StellarUtils.findXlmBalance;
import static utils.StringUtils.redactWithEllipsis;

public class StellarGetProfileOperation implements GetProfileOperation {
    private static final Logger.ALogger logger = Logger.of(StellarGetProfileOperation.class);

    private StellarServerAndNetwork serverAndNetwork;
    private StellarBlockchainConfiguration configuration;

    @Override
    public void init(BlockchainConfiguration configuration) {
        this.configuration = (StellarBlockchainConfiguration) configuration;
        serverAndNetwork = StellarServerAndNetwork.create(this.configuration);
    }

    @Override
    public void useTestNet() {
        serverAndNetwork = StellarServerAndNetwork.createForTestNet(this.configuration);
    }

    @Override
    public String getProfileCid(Account account) {
        Server server = serverAndNetwork.getServer();

        try {
            AccountResponse accountResponse = server.accounts().account(account.publik);
            byte[] cidBytes = accountResponse.getData().getDecoded("galacticPubProfile");
            return new String(cidBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
