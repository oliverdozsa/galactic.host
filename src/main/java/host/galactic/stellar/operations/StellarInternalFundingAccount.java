package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import io.quarkus.runtime.configuration.ConfigUtils;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.stellar.sdk.KeyPair;

import java.util.List;

@ApplicationScoped
public class StellarInternalFundingAccount {
    @ConfigProperty(name = "galactic.pub.internal.funding.account.secret")
    private String internalFundingAccountSecret;

    private KeyPair internalFundingAccount;

    public KeyPair get() {
        if(internalFundingAccount == null) {
            determineAccount();
        }

        return internalFundingAccount;
    }

    private void determineAccount() {
        List<String> profiles = ConfigUtils.getProfiles();

        String mode = profiles.get(0);
        if(profiles.size() == 1 && (mode.equals("dev") || mode.equals("test"))) {
            Log.infof("get(): App is in %s mode; using random internal funding account.", mode);
            internalFundingAccount = KeyPair.random();
        } else {
            Log.info("get(): Using predefined internal funding account.");
            internalFundingAccount = KeyPair.fromSecretSeed(internalFundingAccountSecret);
        }
    }


}
