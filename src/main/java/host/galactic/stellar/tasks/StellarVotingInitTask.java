package host.galactic.stellar.tasks;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.operations.*;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;

import java.util.Collections;
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

    private Uni<Void> initializeVotingIfNeeded(VotingEntity voting) {
        if(voting == null) {
            Log.debugf("%s: Not found any voting to initialize.", taskId);
            return Uni.createFrom().voidItem();
        }

        var channelInit = initializeChannelGeneratorsIfNeeded(voting);
        var assetAccountsInit = initializeAssetAccountsIfNeeded(voting);

        // TODO: merge the Unis.

        return Uni.createFrom().voidItem();
    }

    private Uni<Void> initializeChannelGeneratorsIfNeeded(VotingEntity voting) {
        // TODO
        return Uni.createFrom().voidItem();
    }

    private Uni<Void> initializeAssetAccountsIfNeeded(VotingEntity voting) {
        // TODO
        return Uni.createFrom().voidItem();
    }

    private Uni<StellarChannelGenerators> createChannelGeneratorsForVotingIfNeeded(VotingEntity votingEntity) {
        if(votingEntity != null) {
            Log.debugf("cg size=%s, voting id=%s", votingEntity.channelGenerators.size(), votingEntity.id);
        }

        if(votingEntity == null) {
            Log.debugf("%s: Not found any uninitialized voting.", taskId);
            return Uni.createFrom().item(() -> new StellarChannelGenerators(null, null));
        }

        if (votingEntity.channelGenerators.isEmpty()) {
            Log.infof("%s: Found a voting without channel generators! voting id = %s", taskId, votingEntity.id);

            var stellarOperations = context.operationsProducer().create(votingEntity.isOnTestNetwork);
            var payload = new StellarChannelGeneratorOperationPayload(
                    votingEntity.fundingAccountSecret,
                    votingEntity.maxVoters,
                    votingEntity.id,
                    context.voteBuckets()
            );

            return stellarOperations.createChannelGenerators(payload)
                    .map(accounts -> new StellarChannelGenerators(votingEntity, accounts));
        } else {
            Log.debugf("%s: Voting has channel generators already, but not asset accounts! voting id = %s", taskId, votingEntity.id);
            return Uni.createFrom().item(() -> new StellarChannelGenerators(votingEntity, Collections.emptyList()));
        }
    }

    private Uni<VotingEntity> createChannelGeneratorEntitiesFromIfNeeded(StellarChannelGenerators stellarChannelGenerators) {
        return context.channelGeneratorRepository().createFrom(stellarChannelGenerators.generatorAccounts())
                .map(v -> stellarChannelGenerators.votingEntity());
    }

    private Uni<StellarAssetAccounts> createAssetAccounts(VotingEntity votingEntity) {
        if(votingEntity == null) {
            Log.debugf("");
        }

        if(votingEntity.distributionAccountSecret == null) {
            Log.infof("%s: Found a voting without asset accounts! voting id = %s", taskId, votingEntity.id);

            var stellarOperation = context.operationsProducer().create(votingEntity.isOnTestNetwork);
            var payload = new StellarAssetAccountsOperationPayload(votingEntity.fundingAccountSecret, votingEntity.id);
            return stellarOperation.createAssetAccounts(payload);
        } else {
            Log.debugf("%s: Not found voting without channel accounts.", taskId);
            return Uni.createFrom().item(() -> null);
        }
    }

    private Uni<Void> setAssetAccountsForVoting(StellarAssetAccounts assetAccounts) {
        return context.votingRepository().assetAccountsCreated(assetAccounts);
    }
}
