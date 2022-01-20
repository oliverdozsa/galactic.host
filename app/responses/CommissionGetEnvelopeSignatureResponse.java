package responses;

import static utils.StringUtils.redactWithEllipsis;

public class CommissionGetEnvelopeSignatureResponse {
    private String envelopeSignatureBase64;

    public String getEnvelopeSignatureBase64() {
        return envelopeSignatureBase64;
    }

    public void setEnvelopeSignatureBase64(String envelopeSignatureBase64) {
        this.envelopeSignatureBase64 = envelopeSignatureBase64;
    }

    @Override
    public String toString() {
        return "CommissionGetEnvelopeSignatureResponse{" +
                "envelopeSignatureBase64='" + redactWithEllipsis(envelopeSignatureBase64, 5) + '\'' +
                '}';
    }
}
