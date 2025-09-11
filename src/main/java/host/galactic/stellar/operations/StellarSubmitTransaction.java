package host.galactic.stellar.operations;

import io.quarkus.logging.Log;
import org.stellar.sdk.Server;
import org.stellar.sdk.Transaction;
import org.stellar.sdk.exception.BadRequestException;

import java.util.StringJoiner;

public class StellarSubmitTransaction {
    public static void submit(String name, Transaction transaction, Server server) {
        Log.infof("[STELLAR]: Submitting %s transaction with operations: %s", name, collectionOperationsOf(transaction));

        try {
            var response = server.submitTransaction(transaction);
            if(response.getSuccessful()) {
                Log.info("[STELLAR]: Successfully submitted transaction!");
            }
        } catch (BadRequestException badRequestException) {
            Log.errorf("[STELLAR]: Transaction failed: bad request.");
            Log.errorf("[STELLAR]: Problem details: %s", badRequestException.getProblem());
            throw new StellarOperationsException("[STELLAR]: bad request", badRequestException);
        } catch (Exception e) {
            throw new StellarOperationsException("[STELLAR]: Submitting transaction failed.", e);
        }
    }

    private static String collectionOperationsOf(Transaction transaction) {
        StringJoiner joiner = new StringJoiner(", ");
        for (var operation : transaction.getOperations()) {
            String operationName =
                    operation.getClass().getCanonicalName()
                            .replace("Operation", "")
                            .replace("org.stellar.sdk.operations", "");
            joiner.add(operationName);
        }

        return joiner.toString();
    }
}
