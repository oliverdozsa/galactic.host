package host.galactic.stellar.rest;

import host.galactic.stellar.rest.requests.commission.CommissionInitRequest;
import host.galactic.stellar.rest.responses.commission.CommissionInitResponse;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/stellar/commission")
public class StellarCommissionRest {
    @Inject
    private StellarCommissionInitSessionRest initRest;

    @Path("/initsession")
    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommissionInitResponse> initSession(@Valid CommissionInitRequest request) {
        return initRest.initSession(request);
    }
}
