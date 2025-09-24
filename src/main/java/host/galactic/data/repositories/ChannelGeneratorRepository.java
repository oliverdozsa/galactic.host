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
        Log.infof("create(): Storing %d channel generators for voting %d.", channelGenerators.size(), channelGenerators.get(0).votingId());

        var entities = channelGenerators.stream().map(this::toEntity);
        return persist(entities);
    }

    public Uni<List<ChannelGeneratorEntity>> notFinishedSampleOf(int sampleSize) {
        return find("where accountsLeftToCreate > 0")
                .page(0, sampleSize)
                .list();
    }

    private ChannelGeneratorEntity toEntity(StellarChannelGenerator stellarChannelGenerator) {
        var entity = new ChannelGeneratorEntity();
        entity.accountSecret = stellarChannelGenerator.accountSecret();
        entity.accountsLeftToCreate = stellarChannelGenerator.accountsToCreate();
        entity.isRefunded = false;
        entity.voting = new VotingEntity();
        entity.voting.id = stellarChannelGenerator.votingId();

        return entity;
    }
}
