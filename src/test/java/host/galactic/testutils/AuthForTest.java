package host.galactic.testutils;

import io.quarkus.test.keycloak.client.KeycloakTestClient;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Arrays;

@ApplicationScoped
public class AuthForTest {
    private KeycloakTestClient keycloakClient = new KeycloakTestClient();

    public String loginAs(String user) {
        return keycloakClient.getAccessToken(
                user,
                user,
                "quarkus-app",
                "secret",
                Arrays.asList("openid"));
    }
}
