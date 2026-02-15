package host.galactic.stellar.rest.services.commission;

import host.galactic.stellar.rest.responses.commission.CommissionGetPublicKeyResponse;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@RequestScoped
public class StellarCommissionRestServicePublicKey {
    @Inject
    @Named("signingPublicKeyPem")
    private String publicSigningKey;

    public Uni<CommissionGetPublicKeyResponse> get() {
        Log.info("Got request to get signing key.");
        return Uni.createFrom().item(new CommissionGetPublicKeyResponse(publicSigningKey));
    }
}
