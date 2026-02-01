package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.smallrye.mutiny.vertx.MutinyHelper;
import io.vertx.core.Vertx;
import org.stellar.sdk.*;
import org.stellar.sdk.operations.ChangeTrustOperation;
import org.stellar.sdk.operations.CreateAccountOperation;
import org.stellar.sdk.operations.PaymentOperation;

import java.math.BigDecimal;

import static host.galactic.stellar.operations.StellarUtils.toAssetAmount;

public class StellarCreateVoterAccountTxOperation {
    private Server server;
    private Network network;

    private TransactionBuilder txBuilder;
    private StellarCreateVoterAccountTxPayload payload;

    private KeyPair channelKeyPair;
    private KeyPair distributionKeyPair;
    private KeyPair issuerKeyPair;

    public StellarCreateVoterAccountTxOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<String> create(StellarCreateVoterAccountTxPayload payload) {
        return Uni.createFrom().item(() -> {
                    this.payload = payload;
                    channelKeyPair = KeyPair.fromSecretSeed(payload.channelAccountSecret());
                    distributionKeyPair = KeyPair.fromSecretSeed(payload.distributionAccountSecret());
                    issuerKeyPair = KeyPair.fromSecretSeed(payload.issuerAccountSecret());

                    prepareTxBuilder();
                    createVoterAccount();
                    allowVoterToHaveVoteToken();
                    sendOneTokenToVoter();

                    var transaction = createSignedTransaction();
                    return transaction.toEnvelopeXdrBase64();
                })
                .runSubscriptionOn(Infrastructure.getDefaultExecutor())
                .emitOn(MutinyHelper.executor(Vertx.currentContext()));
    }

    private void prepareTxBuilder() {
        var channelAccount = server.loadAccount(channelKeyPair.getAccountId());
        txBuilder = new TransactionBuilder(channelAccount, network);
    }

    private void createVoterAccount() {
        var operation = CreateAccountOperation.builder()
                .destination(payload.voterAccountPublic())
                .startingBalance(new BigDecimal(2))
                .sourceAccount(distributionKeyPair.getAccountId())
                .build();
        txBuilder.addOperation(operation);
    }

    private void allowVoterToHaveVoteToken() {
        var asset = Asset.create(null, payload.assetCode(), issuerKeyPair.getAccountId());
        var changeTrustAsset = new ChangeTrustAsset(asset);
        var assetLimit = toAssetAmount(payload.maxVoters());

        var operation = ChangeTrustOperation.builder()
                .asset(changeTrustAsset)
                .limit(assetLimit)
                .sourceAccount(payload.voterAccountPublic())
                .build();
        txBuilder.addOperation(operation);
    }

    private void sendOneTokenToVoter() {
        var asset = Asset.create(null, payload.assetCode(), issuerKeyPair.getAccountId());
        var operation = PaymentOperation.builder()
                .destination(payload.voterAccountPublic())
                .asset(asset)
                .amount(toAssetAmount(1))
                .sourceAccount(distributionKeyPair.getAccountId())
                .build();
        txBuilder.addOperation(operation);
    }

    private Transaction createSignedTransaction() {
        var transaction = txBuilder.build();
        transaction.sign(channelKeyPair);
        transaction.sign(distributionKeyPair);

        return transaction;
    }
}
