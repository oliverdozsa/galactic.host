package requests.social;

import play.data.validation.Constraints;

import static utils.StringUtils.redactWithEllipsis;

public class SignupRequest {
    @Constraints.Required
    private String network;

    @Constraints.Required
    private String accountPublic;

    @Constraints.Required
    private String accountSecret;

    private String preferredUserName;

    private boolean useTestnet;

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAccountPublic() {
        return accountPublic;
    }

    public void setAccountPublic(String accountPublic) {
        this.accountPublic = accountPublic;
    }

    public String getAccountSecret() {
        return accountSecret;
    }

    public void setAccountSecret(String accountSecret) {
        this.accountSecret = accountSecret;
    }

    public String getPreferredUserName() {
        return preferredUserName;
    }

    public void setPreferredUserName(String preferredUserName) {
        this.preferredUserName = preferredUserName;
    }

    public boolean isUseTestnet() {
        return useTestnet;
    }

    public void setUseTestnet(boolean useTestnet) {
        this.useTestnet = useTestnet;
    }

    @Override
    public String toString() {
        return "SignupRequest{" +
                "network='" + network + '\'' +
                ", accountPublic='" + redactWithEllipsis(accountPublic, 5) + '\'' +
                ", accountSecret='" + redactWithEllipsis(accountSecret, 5) + '\'' +
                ", preferredUserName='" + preferredUserName + '\'' +
                ", useTestnet='" + useTestnet + '\'' +
                '}';
    }
}
