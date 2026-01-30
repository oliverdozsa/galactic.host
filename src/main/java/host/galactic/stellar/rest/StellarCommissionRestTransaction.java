package host.galactic.stellar.rest;

import host.galactic.stellar.rest.requests.commission.CommissionCreateTransactionRequest;
import host.galactic.stellar.rest.responses.commission.CommissionCreateTransactionResponse;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class StellarCommissionRestTransaction {
    public Uni<CommissionCreateTransactionResponse> create(CommissionCreateTransactionRequest request) {
        var message = request.message();
        var truncatedMessage = message.length() >= 10 ? message.substring(0, 10) + "..." : message;

        var signature = request.revealedSignatureBase64();
        var truncatedSignature = signature.length() > 10 ? signature.substring(0, 10) + "..." : signature;

        Log.infof("Got request to create transaction for message %s and signature %s", message, signature);
        // TODO: 1. Check that signature is valid for message
        // TODO: 2. Parse the message. It's in format: votingId|voterPublic. It's assumed that voterPublic doesn't exist
        // TODO: 3. Check that voting exists
        // TODO: 4. Ensure that asset accounts are created for voting
        // TODO: 5. Consume a channel account from voting
        // TODO: 6. Using the channel account, the distribution account, and the voter public, form the transaction:
        // TODO     That creates voter account, and transfers 1 vote token to it.
        // TODO: 7. Return the transaction as envelopeXdrBase64

        return null;
    }
}
