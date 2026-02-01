package host.galactic.stellar.rest.requests.commission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CommissionCreateTransactionRequest(
        @Pattern(regexp = "[0-9]+\\|.+")
        String message,

        @NotBlank
        String revealedSignatureBase64
) {
}
