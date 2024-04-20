package galactic.blockchain.stellar.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.SignupOperation;
import galactic.blockchain.stellar.StellarBlockchainConfiguration;
import galactic.blockchain.stellar.StellarServerAndNetwork;
import galactic.blockchain.stellar.StellarSubmitTransaction;
import galactic.blockchain.stellar.StellarUtils;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.AccountResponse;
import play.Logger;

import java.io.IOException;
import java.math.BigDecimal;

import static galactic.blockchain.stellar.StellarUtils.findXlmBalance;
import static galactic.blockchain.stellar.StellarUtils.fromAccount;
import static utils.StringUtils.redactWithEllipsis;

public class StellarSignupOperation implements SignupOperation {
    private static final Logger.ALogger logger = Logger.of(StellarSignupOperation.class);

    private StellarServerAndNetwork serverAndNetwork;
    private StellarBlockchainConfiguration configuration;

    private String minimumBalanceForSignup;

    @Override
    public void init(BlockchainConfiguration configuration) {
        this.configuration = (StellarBlockchainConfiguration) configuration;
        serverAndNetwork = StellarServerAndNetwork.create(this.configuration);

        StellarBlockchainConfiguration stellarBlockchainConfig = ((StellarBlockchainConfiguration) configuration);

        minimumBalanceForSignup = stellarBlockchainConfig.getSocialMinimumSpendableBalanceForSignupOf("stellar");
    }

    @Override
    public void useTestNet() {
        serverAndNetwork = StellarServerAndNetwork.createForTestNet(this.configuration);
    }

    @Override
    public boolean isAccountValid(Account account) {
        boolean isValid;

        try {
            KeyPair keyPair = StellarUtils.fromAccount(account);
            isValid = account.publik.equals(keyPair.getAccountId());

            if (!isValid) {
                logger.warn("[STELLAR]: Signup account ");
            }
        } catch (Exception e) {
            logger.warn("[STELLAR]: Signup account is not a valid Stellar account.", e);
            isValid = false;
        }

        String accountPublicToLog = redactWithEllipsis(account.publik, 5);
        String accountSecretToLog = redactWithEllipsis(account.secret, 5);
        logger.info("[STELLAR]: Signup account with public: {} and secret: {} is {}",
                accountPublicToLog, accountSecretToLog, isValid ? "valid" : "not valid");

        return isValid;
    }

    @Override
    public boolean hasEnoughBalance(Account account) {
        Server server = serverAndNetwork.getServer();

        try {
            AccountResponse accountResponse = server.accounts().account(account.publik);
            BigDecimal accountBalance = findXlmBalance(accountResponse.getBalances());
            BigDecimal minimumBalanceAsBigDecimal = new BigDecimal(minimumBalanceForSignup);

            logger.info("[STELLAR]: Signup account has balance (XLM): {}, minimum balance: {}",
                    accountBalance, minimumBalanceForSignup);

            return accountBalance.compareTo(minimumBalanceAsBigDecimal) > 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setProfileCid(Account account, String cid) {
        try {
            Transaction.Builder setProfileCidTx = prepareTransaction(account);
            prepareManageDataOperationOn(setProfileCidTx, cid);
            submitTransaction(setProfileCidTx, account);
        } catch (IOException | AccountRequiresMemoException e) {
            throw new RuntimeException(e);
        }

    }

    private Transaction.Builder prepareTransaction(Account profileAccount) throws IOException {
        Server server = serverAndNetwork.getServer();
        Network network = serverAndNetwork.getNetwork();

        return StellarUtils.createTransactionBuilder(server, network, profileAccount.publik);
    }

    private void prepareManageDataOperationOn(Transaction.Builder txBuilder, String cid) {
        ManageDataOperation.Builder manageDataOperationBuilder = new ManageDataOperation.Builder("galacticPubProfile", cid.getBytes());
        txBuilder.addOperation(manageDataOperationBuilder.build());

        logger.info("[STELLAR]: About to save profile cid: {}", redactWithEllipsis(cid, 10));
    }

    private void submitTransaction(Transaction.Builder txBuilder, Account account) throws AccountRequiresMemoException, IOException {
        Transaction transaction = txBuilder.build();
        KeyPair keyPair = fromAccount(account);

        transaction.sign(keyPair);

        Server server = serverAndNetwork.getServer();
        StellarSubmitTransaction.submit("save profile cid", transaction, server);
    }
}
