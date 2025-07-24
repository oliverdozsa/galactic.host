package host.galactic.stellar.operations;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.quarkus.runtime.Startup;
import io.quarkus.logging.Log;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@ApplicationScoped
public class StellarStartup {
    @RestClient
    StellarFriendBotClient friendBotClient;

    @Inject
    StellarInternalFundingAccount internalFundingAccount;

    @Startup
    void onStartup() {
        Log.info("onStartup(): executing startup actions.");

        List<String> profiles = ConfigUtils.getProfiles();
        String mode = profiles.get(0);
        if(profiles.size() == 1 && (mode.equals("dev") || mode.equals("prod"))) {
            String accountId = internalFundingAccount.keypair().getAccountId();
            Log.infof("onStartup(): App is in %s mode; trying to create internal funding account on testnet with id = %s", mode, accountId);

            try {
                friendBotClient.createAccount(accountId);
            } catch (Exception e) {
                Log.warn("onStartup(): Failed to create funding account on test network.", e);
            }
        }
    }
}
