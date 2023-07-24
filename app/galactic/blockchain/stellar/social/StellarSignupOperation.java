package galactic.blockchain.stellar.social;

import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.BlockchainException;
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
    public void deductSignupCost(Account source, Account destination) {
        String sourceAccountToLog = redactWithEllipsis(source.publik, 5);
        String destinationAccountToLog = redactWithEllipsis(destination.publik, 5);
        logger.info("[STELLAR]: Deducting signup cost of {} XLM from {} to {}",
                minimumBalanceForSignup, sourceAccountToLog, destinationAccountToLog);

        try {
            Transaction.Builder txBuilder = prepareSignupTx(source);
            deductSignupCost(txBuilder, destination);
            submitDeductSignupTx(txBuilder, source);
        } catch (Exception e) {
            logger.warn("[STELLAR]: Failed to deduct signup cost!", e);
            throw new BlockchainException("Failed to deduct signup cost!", e);
        }
    }

    private Transaction.Builder prepareSignupTx(Account source) throws IOException {
        return StellarUtils.createTransactionBuilder(serverAndNetwork.getServer(), serverAndNetwork.getNetwork(), source.publik);
    }

    private void deductSignupCost(Transaction.Builder txBuilder, Account destination) {
        PaymentOperation signupPayment = new PaymentOperation.Builder(destination.publik, new AssetTypeNative(), minimumBalanceForSignup)
                .build();
        txBuilder.addOperation(signupPayment);
    }

    private void submitDeductSignupTx(Transaction.Builder txBuilder, Account source) throws AccountRequiresMemoException, IOException {
        KeyPair sourceKeyPair = StellarUtils.fromAccount(source);
        Transaction deductSignupCostTx = txBuilder.build();
        deductSignupCostTx.sign(sourceKeyPair);

        StellarSubmitTransaction.submit("deduct signup cost", deductSignupCostTx, serverAndNetwork.getServer());
    }
}
