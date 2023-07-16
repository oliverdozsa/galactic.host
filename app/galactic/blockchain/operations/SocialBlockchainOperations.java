package galactic.blockchain.operations;

import com.typesafe.config.Config;
import exceptions.BusinessLogicViolationException;
import executioncontexts.BlockchainExecutionContext;
import galactic.blockchain.Blockchains;
import galactic.blockchain.api.Account;
import galactic.blockchain.api.social.SignupOperation;
import play.Logger;
import requests.social.SignupRequest;

import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.runAsync;

public class SocialBlockchainOperations {
    private final BlockchainExecutionContext blockchainExecContext;
    private final Blockchains blockchains;
    private final Config config;

    private static final Logger.ALogger logger = Logger.of(SocialBlockchainOperations.class);

    public SocialBlockchainOperations(BlockchainExecutionContext blockchainExecContext, Blockchains blockchains,
                                      Config config) {
        this.blockchainExecContext = blockchainExecContext;
        this.blockchains = blockchains;
        this.config = config;
    }

    CompletionStage<Void> signup(SignupRequest request) {
        return runAsync(() -> {
            logger.info("signup(): request = {}", request);

            SignupOperation signupOperation = blockchains.getFactoryByNetwork(request.network).createSignupOperation();

            Account userAccount = new Account(request.accountSecret, request.accountPublic);
            if(!signupOperation.isAccountValid(userAccount)){
                throw new BusinessLogicViolationException("signup(): Account is not valid.");
            }

            if(!signupOperation.hasEnoughBalance(userAccount)) {
                throw new BusinessLogicViolationException("signup(): Account doesn't have enough balance.");
            }

            signupOperation.deductSignupCost(userAccount);

        }, blockchainExecContext);
    }

    private Account getCostAccount(SignupRequest request) {
        // TODO
        return null;
    }
}
