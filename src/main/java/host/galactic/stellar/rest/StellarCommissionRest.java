package host.galactic.stellar.rest;

import host.galactic.stellar.rest.responses.commission.CommissionGetPublicKeyResponse;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/stellar/commission")
public class StellarCommissionRest {
    @Inject
    private StellarCommissionPublicKeyRest publicKeyRest;

    @Path("/publickey")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommissionGetPublicKeyResponse> initSession() {
        return publicKeyRest.get();
    }
}
