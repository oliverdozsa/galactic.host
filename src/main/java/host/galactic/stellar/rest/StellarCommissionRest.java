package host.galactic.stellar.rest;

import host.galactic.stellar.rest.requests.commission.CommissionCreateTransactionRequest;
import host.galactic.stellar.rest.requests.commission.CommissionGetTransactionOfSignatureRequest;
import host.galactic.stellar.rest.requests.commission.CommissionSignEnvelopeRequest;
import host.galactic.stellar.rest.responses.commission.CommissionCreateTransactionResponse;
import host.galactic.stellar.rest.responses.commission.CommissionGetPublicKeyResponse;
import host.galactic.stellar.rest.responses.commission.CommissionGetTransactionOfSignatureResponse;
import host.galactic.stellar.rest.responses.commission.CommissionSignEnvelopeResponse;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/stellar/commission")
public class StellarCommissionRest {
    @Inject
    StellarCommissionRestPublicKey publicKeyRest;

    @Inject
    StellarCommissionRestSignEnvelope signEnvelope;

    @Inject
    StellarCommissionRestTransaction transaction;

    @Path("/publickey")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommissionGetPublicKeyResponse> initSession() {
        return publicKeyRest.get();
    }

    @Path("/signenvelope/{votingId}")
    @POST
    @Authenticated
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommissionSignEnvelopeResponse> signEnvelope(Long votingId, @Valid CommissionSignEnvelopeRequest request) {
        return signEnvelope.sign(votingId, request);
    }

    @Path("/signature")
    @GET
    @Authenticated
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommissionSignEnvelopeResponse> getSignature(@QueryParam("voting") Long voting) {
        return signEnvelope.getBy(voting);
    }

    @Path("/transaction")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommissionCreateTransactionResponse> createTransaction(CommissionCreateTransactionRequest request) {
        return transaction.create(request);
    }

    @Path("/transactionofsignature")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<CommissionGetTransactionOfSignatureResponse> getTransactionOfSignature(CommissionGetTransactionOfSignatureRequest request) {
        return transaction.getTxOfSignature(request);
    }
}
