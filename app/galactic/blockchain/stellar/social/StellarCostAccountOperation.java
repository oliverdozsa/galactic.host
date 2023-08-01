package galactic.blockchain.stellar.social;

import akka.actor.ActorSystem;
import akka.stream.Materializer;
import galactic.blockchain.api.Account;
import galactic.blockchain.api.BlockchainConfiguration;
import galactic.blockchain.api.social.CostAccountOperation;
import galactic.blockchain.stellar.StellarBlockchainConfiguration;
import galactic.blockchain.stellar.StellarServerAndNetwork;
import org.stellar.sdk.Server;
import org.stellar.sdk.responses.AccountResponse;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.libs.ws.ahc.AhcWSClient;
import play.shaded.ahc.org.asynchttpclient.*;

import java.io.IOException;

import static utils.StringUtils.redactWithEllipsis;

public class StellarCostAccountOperation implements CostAccountOperation {
    private StellarServerAndNetwork serverAndNetwork;
    private StellarBlockchainConfiguration configuration;

    private boolean useTestnet;

    private static final Logger.ALogger logger = Logger.of(StellarCostAccountOperation.class);

    @Override
    public void init(BlockchainConfiguration configuration) {
        this.configuration = (StellarBlockchainConfiguration) configuration;
        serverAndNetwork = StellarServerAndNetwork.create(this.configuration);
    }

    @Override
    public void useTestNet() {
        serverAndNetwork = StellarServerAndNetwork.createForTestNet(this.configuration);
        useTestnet = true;
    }

    @Override
    public Account getAccount() {
        Account account = configuration.getSocialCostAccountOf();

        if (useTestnet) {
            createAccountOnTestnetIfNeeded(account);
        }

        return account;
    }

    private void createAccountOnTestnetIfNeeded(Account account) {
        Server server = serverAndNetwork.getServer();
        String accountPublicToLog = redactWithEllipsis(account.publik, 5);

        try {
            AccountResponse accountResponse = server.accounts().account(account.publik);
            logger.info("[STELLAR]: Cost account: {} on testnet already exists.", accountPublicToLog);
            return;
        } catch (IOException e) {
            logger.info("[STELLAR]: It seems cost account: {} doesn't exist yet; trying to create it.", accountPublicToLog);
        }

        try (WSClient wsClient = initWsClient()) {
            WSRequest request = wsClient.url("https://friendbot.stellar.org/?addr=" + account.publik);
            WSResponse response = request.get()
                    .toCompletableFuture()
                    .get();
            boolean isSuccessful = response.getStatus() >= 200 && response.getStatus() < 300;
            if (!isSuccessful) {
                logger.warn("[STELLAR]: Failed to create cost account, status code: {}", response.getStatus());
                throw new RuntimeException("[STELLAR]: Failed to create cost account, status code: " + response.getStatus());
            }
        } catch (Exception e) {
            logger.warn("[STELLAR]: Failed to create cost account!", e);
            throw new RuntimeException(e);
        }
    }

    private WSClient initWsClient() {
        // Set up Akka
        String name = "wsclient";
        ActorSystem system = ActorSystem.create(name);
        Materializer materializer = Materializer.matFromSystem(system);

        // Set up AsyncHttpClient directly from config
        AsyncHttpClientConfig asyncHttpClientConfig =
                new DefaultAsyncHttpClientConfig.Builder()
                        .setMaxRequestRetry(0)
                        .setShutdownQuietPeriod(0)
                        .setShutdownTimeout(0)
                        .build();
        AsyncHttpClient asyncHttpClient = new DefaultAsyncHttpClient(asyncHttpClientConfig);

        // Set up WSClient instance directly from asynchttpclient.
        return new AhcWSClient(asyncHttpClient, materializer);
    }
}
