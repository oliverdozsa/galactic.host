package host.galactic.stellar.rest.filters;

import io.quarkus.logging.Log;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;


import java.util.List;

public class JwtEmailFilter {
    @Inject
    JsonWebToken jwt;

    @ServerRequestFilter(nonBlocking = true)
    public Response filter(ContainerRequestContext context) {
        if(context.getSecurityContext().getAuthenticationScheme() == null) {
            return null;
        }

        if(!jwt.getClaimNames().containsAll(List.of("email", "email_verified"))) {
            Log.warn("filter(): email, and email_verified claims must both be present.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        boolean isEmailVerified = jwt.getClaim("email_verified");
        if(!isEmailVerified) {
            Log.warnf("filter(): \"%s\" is not verified.", jwt.claim("email").get());
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        return null;
    }
}
