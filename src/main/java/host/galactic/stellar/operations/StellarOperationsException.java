package host.galactic.stellar.operations;

public class StellarOperationsException extends RuntimeException {
    public StellarOperationsException(String message) {
        super(message);
    }

    public StellarOperationsException(String message, Throwable cause) {
        super(message, cause);
    }
}
