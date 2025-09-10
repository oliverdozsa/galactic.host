package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.responses.TransactionResponse;

import java.util.List;
import java.util.StringJoiner;

public class StellarSubmitTransaction {
    public static void submit(String name, Transaction transaction, Server server) {
        Log.infof("[STELLAR]: Submitting {} transaction with operations: {}", name, collectionOperationsOf(transaction));
        var response = server.submitTransaction(transaction);

        if (response.getSuccessful()) {
            Log.info("[STELLAR]: Successfully submitted transaction!");
        } else {
            String logMessage = String.format("[STELLAR]: Failed to submit transaction!");
            Log.error(logMessage);
            throw new StellarOperationsException(logMessage);
        }
    }

    private static String collectionOperationsOf(Transaction transaction) {
        StringJoiner joiner = new StringJoiner(", ");
        for (var operation : transaction.getOperations()) {
            String operationName =
                    operation.getClass().getCanonicalName()
                            .replace("Operation", "")
                            .replace("org.stellar.sdk.", "");
            joiner.add(operationName);
        }

        return joiner.toString();
    }
}
