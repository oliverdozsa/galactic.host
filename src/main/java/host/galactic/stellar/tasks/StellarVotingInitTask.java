package host.galactic.stellar.tasks;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.operations.StellarAssetAccounts;
import host.galactic.stellar.operations.StellarChannelGenerator;
import host.galactic.stellar.operations.StellarChannelGeneratorOperationPayload;
import host.galactic.stellar.operations.StellarChannelGenerators;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.function.Function;

public class StellarVotingInitTask implements Function<ScheduledExecution, Uni<Void>> {
    private StellarVotingInitContext context;
    private String taskId;

    StellarVotingInitTask(StellarVotingInitContext context) {
        this.context = context;
    }

    @Override
    public Uni<Void> apply(ScheduledExecution execution) {
        return context.sessionFactory().withSession(s -> {
            taskId = execution.getTrigger().getId();
            return context.votingRepository().getAnUninitializedVoting()
                    .chain(this::createChannelGeneratorsForVotingIfNeeded)
                    .chain(this::createChannelGeneratorEntitiesFromIfNeeded)
                    .chain(this::createAssetAccounts)
                    .chain(this::setAssetAccountsForVoting);
        });
    }

    private Uni<StellarChannelGenerators> createChannelGeneratorsForVotingIfNeeded(VotingEntity votingEntity) {
        if (votingEntity != null) {
            Log.infof("%s: Found an uninitialized voting with id = %s!", taskId, votingEntity.id);

            var stellarOperations = context.operationsProducer().create(votingEntity.isOnTestNetwork);

            var payload = new StellarChannelGeneratorOperationPayload(
                    votingEntity.fundingAccountSecret,
                    votingEntity.maxVoters,
                    votingEntity.id,
                    context.voteBuckets()
            );

            return stellarOperations.createChannelGenerators(payload)
                    .map(accounts -> new StellarChannelGenerators(votingEntity.id, accounts));
        } else {
            Log.debugf("%s: Not found any uninitialized voting.", taskId);
            return Uni.createFrom().item(() -> null);
        }
    }

    private Uni<StellarChannelGenerators> createChannelGeneratorEntitiesFromIfNeeded(StellarChannelGenerators stellarChannelGenerators) {
        if (stellarChannelGenerators != null) {
            return context.channelGeneratorRepository().createFrom(stellarChannelGenerators.generatorAccounts())
                    .map(v -> stellarChannelGenerators);
        } else {
            return Uni.createFrom().item(() -> null);
        }
    }

    private Uni<StellarAssetAccounts> createAssetAccounts(StellarChannelGenerators generators) {
        // TODO
        return Uni.createFrom().item(() -> null);
    }

    private Uni<Void> setAssetAccountsForVoting(StellarAssetAccounts assetAccounts) {
        // TODO
        return Uni.createFrom().item(() -> null);
    }
}
