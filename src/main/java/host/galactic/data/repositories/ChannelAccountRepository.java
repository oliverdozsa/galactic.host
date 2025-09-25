package host.galactic.data.repositories;

import host.galactic.data.entities.ChannelAccountEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.operations.StellarChannelAccount;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ChannelAccountRepository implements PanacheRepository<ChannelAccountEntity> {
    @Inject
    private ChannelGeneratorRepository channelGeneratorRepository;

    @WithTransaction
    public Uni<Void> channelAccountsCreated(List<StellarChannelAccount> stellarChannelAccounts, Long channelGeneratorId) {
        var votingId = stellarChannelAccounts.get(0).votingId();
        Log.infof("channelAccountsCreated(): creating %d channel account for voting with id %d",
                stellarChannelAccounts.size(), votingId);
        var entitiesToPersist = stellarChannelAccounts.stream().map(this::toEntity);

        return persist(entitiesToPersist)
                .chain(v -> channelGeneratorRepository.findById(channelGeneratorId))
                .chain(e -> {
                    e.accountsLeftToCreate -= stellarChannelAccounts.size();
                    return channelGeneratorRepository.persist(e);
                })
                .replaceWithVoid();
    }

    private ChannelAccountEntity toEntity(StellarChannelAccount stellarChannelAccount) {
        ChannelAccountEntity entity = new ChannelAccountEntity();
        entity.accountSecret = stellarChannelAccount.accountSecret();
        entity.isConsumed = false;
        entity.isRefunded = false;
        entity.voting = new VotingEntity();
        entity.voting.id = stellarChannelAccount.votingId();

        return entity;
    }
}
