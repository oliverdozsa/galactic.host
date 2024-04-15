package galactic.blockchain.operations;

import com.typesafe.config.Config;
import exceptions.BusinessLogicViolationException;
import executioncontexts.BlockchainExecutionContext;
import galactic.blockchain.Blockchains;
import galactic.blockchain.api.Account;
import galactic.blockchain.api.social.SignupOperation;
import ipfs.api.IpfsApi;
import play.Logger;
import play.libs.ws.WSClient;
import requests.social.SignupRequest;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.runAsync;

public class SocialBlockchainOperations {
    private final BlockchainExecutionContext blockchainExecContext;
    private final Blockchains blockchains;
    private final Config config;
    private final WSClient wsClient;
    private final IpfsApi ipfsApi;

    private static final Logger.ALogger logger = Logger.of(SocialBlockchainOperations.class);

    @Inject
    public SocialBlockchainOperations(BlockchainExecutionContext blockchainExecContext, Blockchains blockchains,
                                      Config config, WSClient wsClient, IpfsApi ipfsApi) {
        this.blockchainExecContext = blockchainExecContext;
        this.blockchains = blockchains;
        this.config = config;
        this.wsClient = wsClient;
        this.ipfsApi = ipfsApi;
    }

    public CompletionStage<Void> signup(SignupRequest request, String encryptionKey) {
        return runAsync(() -> {
            logger.info("signup(): request = {}", request);

            SignupOperation signupOperation = blockchains.getFactoryByNetwork(request.getNetwork()).createSignupOperation();

            Account userAccount = new Account(request.getAccountSecret(), request.getAccountPublic());
            if (!signupOperation.isAccountValid(userAccount)) {
                throw new BusinessLogicViolationException("signup(): Account is not valid.");
            }

            if (!signupOperation.hasEnoughBalance(userAccount)) {
                throw new BusinessLogicViolationException("signup(): Account doesn't have enough balance.");
            }

            // TODO: create profile

        }, blockchainExecContext);
    }
}
