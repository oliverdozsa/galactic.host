package host.galactic.stellar.rest.filters;

import host.galactic.data.repositories.UserRepository;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;

public class AuthenticationFilter {
    @Inject
    UserInfo userInfo;

    @Inject
    UserRepository userRepository;

    @ServerRequestFilter()
    public Uni<Response> filter(ContainerRequestContext context) {
        Log.infof("filter(): auth scheme = %s", context.getSecurityContext().getAuthenticationScheme());
        if (context.getSecurityContext().getAuthenticationScheme() == null) {
            return null;
        }

        if(userInfo.getEmail() == null) {
            Log.warn("filter(): User's email is null; preventing access.");
            return Uni.createFrom().item(Response.status(Response.Status.FORBIDDEN).build());
        }

        Log.info("filter(): Will create user entity if not already in DB.");

        return userRepository.createIfNotExists(userInfo.getEmail())
                .map(u -> null);
    }
}
