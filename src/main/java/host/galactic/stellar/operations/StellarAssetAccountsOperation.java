package host.galactic.stellar.operations;

import host.galactic.data.entities.VotingEntity;
import io.smallrye.mutiny.Uni;
import org.stellar.sdk.*;
import org.stellar.sdk.xdr.Transaction;

import java.math.BigDecimal;

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
        voting = payload.votingEntity();

        var fundingKeyPair = KeyPair.fromSecretSeed(payload.fundingSecret());
        var fundingAccount = server.loadAccount(fundingKeyPair.getAccountId());
        txBuilder = new TransactionBuilder(fundingAccount, network);

        var distributionStartingBalance = new BigDecimal(payload.votingEntity().maxVoters * 2);
        distributionKeyPair = prepareAccountCreation(distributionStartingBalance);
        ballotKeyPair = prepareAccountCreation(new BigDecimal(2));
        issuerKeyPair = prepareAccountCreation(new BigDecimal(2));

        prepareToken();

        // TODO

        return null;
    }

    private KeyPair prepareAccountCreation(BigDecimal startingBalanceXlm) {
        var newAccount = KeyPair.random();

        // TODO

        return newAccount;
    }

    private void prepareToken() {
        allowAccountsToHaveVoteTokens(distributionKeyPair, ballotKeyPair);
        sendAllVoteTokenToDistribution();
        lockIssuer();
    }

    private void allowAccountsToHaveVoteTokens(KeyPair... accounts) {
        var asset = Asset.create(null, voting.assetCode, issuerKeyPair.getAccountId());

        // TODO
    }

    private void sendAllVoteTokenToDistribution() {
        // TODO
    }

    private void lockIssuer() {
        // TODO
    }
}
