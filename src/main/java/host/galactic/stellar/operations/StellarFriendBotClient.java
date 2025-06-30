package host.galactic.stellar.operations;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
public interface StellarFriendBotClient {
    @GET
    void createAccount(@QueryParam("addr") String accountId);
}
