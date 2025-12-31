package host.galactic.stellar.rest.requests.commission;

import jakarta.validation.constraints.NotBlank;

public record CommissionSignEnvelopeRequest(
        @NotBlank(message = "Envelope cannot be blank.")
        String envelopeBase64
) {
}
