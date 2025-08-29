package host.galactic.data.repositories;

import host.galactic.data.entities.ChannelGeneratorEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ChannelGeneratorRepository implements PanacheRepository<ChannelGeneratorEntity> {
    @WithTransaction
    public Uni<ChannelGeneratorEntity> create() {
        Log.info("create()");

        return persist(new ChannelGeneratorEntity());
    }
}
