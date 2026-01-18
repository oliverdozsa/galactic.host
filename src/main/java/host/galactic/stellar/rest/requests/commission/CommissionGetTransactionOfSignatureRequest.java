package host.galactic.stellar.rest.requests.commission;

import jakarta.validation.constraints.NotBlank;

public record CommissionGetTransactionOfSignatureRequest (
        @NotBlank
        String signature
) {
}
