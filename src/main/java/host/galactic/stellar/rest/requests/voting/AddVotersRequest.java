package host.galactic.stellar.rest.requests.voting;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AddVotersRequest (
        @Size(min = 1, message = "Must have at least 1 email.")
        List<@Email(message = "Invalid email.") String> emails
) {
}
