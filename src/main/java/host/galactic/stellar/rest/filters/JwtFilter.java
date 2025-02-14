package host.galactic.stellar.rest.filters;

import host.galactic.data.repositories.UserRepository;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;


import java.util.List;

public class JwtFilter {
    @Inject
    JsonWebToken jwt;

    @Inject
    UserRepository userRepository;

    @ServerRequestFilter()
    public Uni<Response> filter(ContainerRequestContext context) {
        if (context.getSecurityContext().getAuthenticationScheme() == null) {
            return null;
        }

        if (!jwt.getClaimNames().containsAll(List.of("email", "email_verified"))) {
            Log.warn("filter(): email, and email_verified claims must both be present.");
            return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).build());
        }

        boolean isEmailVerified = jwt.getClaim("email_verified");
        if (!isEmailVerified) {
            Log.warnf("filter(): \"%s\" is not verified.", jwt.claim("email").get());
            return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).build());
        }

        Log.info("filter(): JWT is OK, will create user entity if not already in DB.");

        return userRepository.createIfNotExists(jwt.getClaim("email"))
                .map(u -> null);
    }
}
