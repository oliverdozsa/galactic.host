package galactic.blockchain.operations;

import exceptions.BusinessLogicViolationException;
import executioncontexts.BlockchainExecutionContext;
import galactic.blockchain.Blockchains;
import galactic.blockchain.api.Account;
import galactic.blockchain.api.social.SocialOperation;
import ipfs.api.IpfsApi;
import play.Logger;
import requests.social.SignupRequest;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static utils.StringUtils.redactWithEllipsis;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.runAsync;

public class SocialBlockchainOperations {
    private final BlockchainExecutionContext blockchainExecContext;
    private final Blockchains blockchains;

    private static final Logger.ALogger logger = Logger.of(SocialBlockchainOperations.class);

    @Inject
    public SocialBlockchainOperations(BlockchainExecutionContext blockchainExecContext, Blockchains blockchains, IpfsApi ipfsApi) {
        this.blockchainExecContext = blockchainExecContext;
        this.blockchains = blockchains;
    }

    public CompletionStage<Void> signup(SignupRequest request, String actorCid) {
        return runAsync(() -> {
            logger.info("signup(): request = {}, actorCid = {}", request, redactWithEllipsis(actorCid, 10));

            SocialOperation socialOperation = blockchains.getFactoryByNetwork(request.getNetwork()).createSignupOperation();

            Account userAccount = new Account(request.getAccountSecret(), request.getAccountPublic());
            if (!socialOperation.isAccountValid(userAccount)) {
                throw new BusinessLogicViolationException("signup(): Account is not valid.");
            }

            if (!socialOperation.hasEnoughBalance(userAccount)) {
                throw new BusinessLogicViolationException("signup(): Account doesn't have enough balance.");
            }

            socialOperation.setProfileCid(userAccount, actorCid);

        }, blockchainExecContext);
    }

    public CompletionStage<String> getProfileCid(Account account) {
        return supplyAsync(() -> {
            // TODO
            return "";
        }, blockchainExecContext);
    }
}
