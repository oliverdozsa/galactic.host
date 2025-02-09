package host.galactic.stellar.rest.requests.voting;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

import java.util.List;

public record AddVotersRequest (
        List<@Email(message = "Invalid email.") String> emails
) {
}
