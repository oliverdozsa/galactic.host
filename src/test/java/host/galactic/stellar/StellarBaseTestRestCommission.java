package host.galactic.stellar;

import host.galactic.stellar.rest.responses.commission.CommissionSignEnvelopeResponse;

import java.net.URL;

public class StellarBaseTestRestCommission {
    public URL url;

    public StellarBaseTestRestCommission(URL url) {
        this.url = url;
    }

    public CommissionSignEnvelopeResponse signEnvelope(String base64Envelope) {
        // TODO
        return null;
    }
}
