package requests.social;

import play.data.validation.Constraints;

import static utils.StringUtils.redactWithEllipsis;

public class SignupRequest {
    @Constraints.Required
    public String network;

    @Constraints.Required
    public String accountPublic;

    @Constraints.Required
    public String accountSecret;

    public boolean useTestnet;

    @Override
    public String toString() {
        return "SignupRequest{" +
                "network='" + network + '\'' +
                ", accountPublic='" + redactWithEllipsis(accountPublic, 5) + '\'' +
                ", accountSecret='" + redactWithEllipsis(accountSecret, 5) + '\'' +
                ", useTestnet='" + useTestnet + '\'' +
                '}';
    }
}
