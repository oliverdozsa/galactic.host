package host.galactic.stellar.rest.mappers;

import host.galactic.stellar.operations.StellarOperationsException;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;

public class ExceptionMapper {
    public Uni<Response> mapStellarOperationsException(StellarOperationsException e) {
        return Uni.createFrom()
                .failure(new ServerErrorException("Stellar operation failed!", Response.Status.INTERNAL_SERVER_ERROR, e));
    }
}
