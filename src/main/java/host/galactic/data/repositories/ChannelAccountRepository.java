package host.galactic.data.repositories;

import host.galactic.data.entities.ChannelAccountEntity;
import host.galactic.stellar.operations.StellarChannelAccount;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ChannelAccountRepository implements PanacheRepository<ChannelAccountEntity> {
    @WithTransaction
    public Uni<Void> channelAccountsCreated(List<StellarChannelAccount> stellarChannelAccounts) {
        // TODO
        return null;
    }
}
