package host.galactic.stellar.rest;

import host.galactic.stellar.rest.responses.commission.CommissionGetPublicKeyResponse;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
class StellarCommissionPublicKeyRest {
    @WithSession
    public Uni<CommissionGetPublicKeyResponse> get() {
        Log.infof("Got request to init a session.");

        return null;
    }
}
