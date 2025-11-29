package host.galactic.stellar.tasks;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.operations.*;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;

import java.util.Collections;
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
                    .chain(this::initializeVotingIfNeeded);
        });
    }

    private Uni<Void> initializeVotingIfNeeded(VotingEntity voting) {
        if(voting == null) {
            Log.debugf("%s: Not found any voting to initialize.", taskId);
            return Uni.createFrom().voidItem();
        }

        var channelInit = initializeChannelGeneratorsIfNeeded(voting);
        var assetAccountsInit = initializeAssetAccountsIfNeeded(voting);

        return channelInit.chain(() -> assetAccountsInit);
    }

    private Uni<Void> initializeChannelGeneratorsIfNeeded(VotingEntity voting) {
        if(voting == null) {
            Log.debugf("%s: initializeChannelGeneratorsIfNeeded(): Not found any uninitialized voting.", taskId);
            return Uni.createFrom().voidItem();
        }

        return createChannelGeneratorsForVotingIfNeeded(voting)
                .chain(this::createChannelGeneratorEntities);
    }

    private Uni<Void> initializeAssetAccountsIfNeeded(VotingEntity voting) {
        if(voting == null) {
            Log.debugf("%s: initializeAssetAccountsIfNeeded(): Not found any uninitialized voting.", taskId);
            return Uni.createFrom().voidItem();
        }

        return createAssetAccountsIfNeeded(voting)
                .chain(this::setAssetAccountsForVoting);
    }

    private Uni<List<StellarChannelGenerator>> createChannelGeneratorsForVotingIfNeeded(VotingEntity voting) {
        if (voting.channelGenerators.isEmpty()) {
            Log.infof("%s: Found a voting without channel generators! voting id = %s", taskId, voting.id);

            var stellarOperations = context.operationsProducer().create(voting.isOnTestNetwork);
            var payload = new StellarChannelGeneratorOperationPayload(
                    voting.fundingAccountSecret,
                    voting.maxVoters,
                    voting.id,
                    context.voteBuckets()
            );

            return stellarOperations.createChannelGenerators(payload);
        } else {
            Log.debugf("%s: Voting has channel generators already! voting id = %s", taskId, voting.id);
            return Uni.createFrom().item(Collections::emptyList);
        }
    }

    private Uni<Void> createChannelGeneratorEntities(List<StellarChannelGenerator> channelGenerators) {
        return context.channelGeneratorRepository().createFrom(channelGenerators);
    }

    private Uni<StellarAssetAccounts> createAssetAccountsIfNeeded(VotingEntity voting) {
        if(voting.distributionAccountSecret == null) {
            Log.infof("%s: Found a voting without asset accounts! voting id = %s", taskId, voting.id);

            var stellarOperation = context.operationsProducer().create(voting.isOnTestNetwork);
            var payload = new StellarAssetAccountsOperationPayload(voting.fundingAccountSecret, voting.id);
            return stellarOperation.createAssetAccounts(payload);
        } else {
            Log.debugf("%s: Voting has asset accounts already.", taskId);
            return Uni.createFrom().item(() -> null);
        }
    }

    private Uni<Void> setAssetAccountsForVoting(StellarAssetAccounts assetAccounts) {
        return context.votingRepository().assetAccountsCreated(assetAccounts);
    }
}
