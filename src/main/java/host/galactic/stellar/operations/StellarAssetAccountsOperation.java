package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.Network;
import org.stellar.sdk.Server;
import org.stellar.sdk.TransactionBuilder;

import java.math.BigDecimal;

public class StellarAssetAccountsOperation {
    private Server server;
    private Network network;

    public StellarAssetAccountsOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }

    public Uni<StellarAssetAccounts> create(StellarAssetAccountsOperationPayload payload) {
        var fundingKeyPair = KeyPair.fromSecretSeed(payload.fundingSecret());
        var fundingAccount = server.loadAccount(fundingKeyPair.getAccountId());

        var transactionBuilder = new TransactionBuilder(fundingAccount, network);

        var distributionStartingBalance = new BigDecimal(payload.votingEntity().maxVoters * 2);
        var distributionKeyPair = prepareAccountCreationOn(transactionBuilder, distributionStartingBalance);

        var ballotKeyPair = prepareAccountCreationOn(transactionBuilder, new BigDecimal(2));
        var issuerKeyPair = prepareAccountCreationOn(transactionBuilder, new BigDecimal(2));

        // TODO

        return null;
    }

    private KeyPair prepareAccountCreationOn(TransactionBuilder txBuilder, BigDecimal startingBalanceXlm) {
        var newAccount = KeyPair.random();

        // TODO

        return newAccount;
    }
}
