package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;

public interface StellarOperations {
    Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret);
}
