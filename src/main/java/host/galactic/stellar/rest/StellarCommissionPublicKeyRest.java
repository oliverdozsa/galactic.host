package host.galactic.stellar.rest;

import host.galactic.stellar.rest.responses.commission.CommissionGetPublicKeyResponse;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;

@RequestScoped
class StellarCommissionPublicKeyRest {
    @Inject
    @Named("signingPublicKeyPem")
    private String publicSigningKey;

    @WithSession
    public Uni<CommissionGetPublicKeyResponse> get() {
        Log.infof("Got request to get signing key.");

        return Uni.createFrom().item(new CommissionGetPublicKeyResponse(publicSigningKey));
    }
}
