package host.galactic.stellar.operations;

import host.galactic.data.entities.VotingEntity;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.ChangeTrustOperation;
import org.stellar.sdk.operations.CreateAccountOperation;
import org.stellar.sdk.operations.PaymentOperation;
import org.stellar.sdk.operations.SetOptionsOperation;

import java.math.BigDecimal;
import java.util.Arrays;

import static host.galactic.stellar.operations.StellarUtils.*;
import static org.stellar.sdk.AbstractTransaction.MIN_BASE_FEE;

public class StellarAssetAccountsOperation {
    private Server server;
    private Network network;

    private KeyPair distributionKeyPair;
    private KeyPair ballotKeyPair;
    private KeyPair issuerKeyPair;

    private TransactionBuilder txBuilder;

    private VotingEntity voting;

    public StellarAssetAccountsOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<StellarAssetAccounts> create(StellarAssetAccountsOperationPayload payload) {
        return Uni.createFrom().item(() -> {
            voting = payload.votingEntity();

            var fundingKeyPair = KeyPair.fromSecretSeed(payload.fundingSecret());
            var fundingAccount = server.loadAccount(fundingKeyPair.getAccountId());
            txBuilder = new TransactionBuilder(fundingAccount, network);

            Log.infof("[STELLAR]: About to create asset accounts for voting: %s", voting.id);

            var distributionStartingBalance = new BigDecimal(payload.votingEntity().maxVoters * 2);
            distributionKeyPair = prepareNewAccountCreation(distributionStartingBalance);
            ballotKeyPair = prepareNewAccountCreation(new BigDecimal(2));
            issuerKeyPair = prepareNewAccountCreation(new BigDecimal(2));

            prepareToken();

            var transaction = txBuilder.setBaseFee(MIN_BASE_FEE)
                    .setTimeout(15)
                    .build();

            transaction.sign(fundingKeyPair);
            transaction.sign(distributionKeyPair);
            transaction.sign(ballotKeyPair);
            transaction.sign(issuerKeyPair);

            StellarSubmitTransaction.submit("create asset accounts", transaction, server);

            var distributionSecret = new String(distributionKeyPair.getSecretSeed());
            var ballotSecret = new String(ballotKeyPair.getSecretSeed());
            var issuerSecret = new String(issuerKeyPair.getSecretSeed());

            return new StellarAssetAccounts(distributionSecret, ballotSecret, issuerSecret, voting.id);
        })
        .runSubscriptionOn(Infrastructure.getDefaultExecutor())
        .emitOn(MutinyHelper.executor(Vertx.currentContext()));
    }

    private KeyPair prepareNewAccountCreation(BigDecimal startingBalanceXlm) {
        var newAccount = KeyPair.random();

        var createAccountOperation = CreateAccountOperation.builder()
                .destination(newAccount.getAccountId())
                .startingBalance(startingBalanceXlm)
                .build();

        txBuilder.addOperation(createAccountOperation);

        return newAccount;
    }

    private void prepareToken() {
        Log.infof("[STELLAR]: Preparing vote token: %s", voting.assetCode);
        allowAccountsToHaveVoteTokens(distributionKeyPair, ballotKeyPair);
        sendAllVoteTokenToDistribution();
        lockIssuer();
    }

    private void allowAccountsToHaveVoteTokens(KeyPair... accounts) {
        var accountIds = Arrays.stream(accounts)
                .map(StellarUtils::toTruncatedAccountId).toList();
        Log.infof("[STELLAR]:   - Allowing accounts to have vote token: %s", accountIds);

        var asset = createVoteTokenAsAsset();
        var changeTrustAsset = new ChangeTrustAsset(asset);
        var assetLimit = toAssetAmount(voting.maxVoters);

        for (var account : accounts) {
            var operation = ChangeTrustOperation.builder()
                    .sourceAccount(account.getAccountId())
                    .asset(changeTrustAsset)
                    .limit(assetLimit)
                    .build();
            txBuilder.addOperation(operation);
        }
    }

    private void sendAllVoteTokenToDistribution() {
        Log.infof("[STELLAR]:   - Sending all vote token to distribution: %s", toTruncatedAccountId(distributionKeyPair));

        var asset = createVoteTokenAsAsset();
        var assetAmount = toAssetAmount(voting.maxVoters);

        var paymentOperation = PaymentOperation.builder()
                .sourceAccount(issuerKeyPair.getAccountId())
                .destination(distributionKeyPair.getAccountId())
                .asset(asset)
                .amount(assetAmount)
                .build();

        txBuilder.addOperation(paymentOperation);
    }

    private void lockIssuer() {
        Log.infof("[STELLAR]:   - Locking issuer: %s", toTruncatedAccountId(issuerKeyPair));

        var setOptionsOperation = SetOptionsOperation.builder()
                .sourceAccount(issuerKeyPair.getAccountId())
                .masterKeyWeight(0)
                .lowThreshold(1)
                .mediumThreshold(1)
                .highThreshold(1)
                .build();

        txBuilder.addOperation(setOptionsOperation);
    }

    private Asset createVoteTokenAsAsset() {
        return Asset.create(null, voting.assetCode, issuerKeyPair.getAccountId());
    }
}
