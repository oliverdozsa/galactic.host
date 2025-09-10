package host.galactic.stellar.tasks;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.operations.StellarChannelGenerator;
import host.galactic.stellar.operations.StellarChannelGeneratorOperationPayload;
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
                    .chain(this::createChannelGeneratorsForVotingIfNeeded);
        });
    }

    private Uni<Void> createChannelGeneratorsForVotingIfNeeded(VotingEntity votingEntity) {
        if (votingEntity != null) {
            Log.infof("%s: Found an uninitialized voting with id = %s!", taskId, votingEntity.id);

            var stellarOperations = context.operationsProducer().create(votingEntity.isOnTestNetwork);

            var payload = new StellarChannelGeneratorOperationPayload(
                    votingEntity.fundingAccountSecret,
                    votingEntity.maxVoters,
                    votingEntity.id,
                    context.voteBuckets()
            );
            var channelGenerators = stellarOperations.createChannelGenerators(payload);

            return channelGenerators
                    .chain(this::createChannelGeneratorEntitiesFrom);
        } else {
            Log.tracef("%s: Not found any uninitialized voting.", taskId);
            return Uni.createFrom().item(() -> null);
        }
    }

    private Uni<Void> createChannelGeneratorEntitiesFrom(List<StellarChannelGenerator> stellarChannelGenerators) {
        return context.channelGeneratorRepository().createFrom(stellarChannelGenerators);
    }
}
