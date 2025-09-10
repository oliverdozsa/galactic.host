package host.galactic.stellar.rest.mappers;

import host.galactic.stellar.operations.StellarOperationsException;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMapper {
    @ServerExceptionMapper
    public Uni<Response> mapStellarOperationsException(StellarOperationsException e) {
        return Uni.createFrom()
                .failure(new ServerErrorException("Stellar operation failed!", Response.Status.INTERNAL_SERVER_ERROR, e));
    }
}
