package host.galactic.data.repositories;

import host.galactic.data.entities.ChannelGeneratorEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.operations.StellarChannelGenerator;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ChannelGeneratorRepository implements PanacheRepository<ChannelGeneratorEntity> {
    @WithTransaction
    public Uni<Void> createFrom(List<StellarChannelGenerator> channelGenerators) {
        Log.info("create()");

        var entities = channelGenerators.stream().map(this::from);
        return persist(entities);
    }

    private ChannelGeneratorEntity from(StellarChannelGenerator stellarChannelGenerator) {
        var entity = new ChannelGeneratorEntity();
        entity.accountSecret = stellarChannelGenerator.accountSecret();
        entity.accountsLeftToCreate = stellarChannelGenerator.accountsToCreate();
        entity.isRefunded = false;
        entity.voting = new VotingEntity();
        entity.voting.id = stellarChannelGenerator.votingId();

        return entity;
    }
}
