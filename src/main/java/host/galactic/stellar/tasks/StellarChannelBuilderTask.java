package host.galactic.stellar.tasks;

import host.galactic.data.entities.ChannelGeneratorEntity;
import host.galactic.stellar.operations.StellarChannelAccount;
import host.galactic.stellar.operations.StellarChannelAccountOperationPayload;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class StellarChannelBuilderTask implements Function<ScheduledExecution, Uni<Void>> {
    private int id;
    private StellarChannelBuilderContext context;
    private String taskId;

    private static final int MAX_NUM_OF_ACCOUNTS_TO_CREATE_IN_ONE_BATCH = 50;

    StellarChannelBuilderTask(int id, StellarChannelBuilderContext context) {
        this.id = id;
        this.context = context;
    }

    @Override
    public Uni<Void> apply(ScheduledExecution execution) {
        taskId = execution.getTrigger().getId();

        return context.sessionFactory().withSession(s -> context.channelGeneratorRepository().notFinishedSampleOf(context.voteBuckets())
                .chain(this::selectChannelGenerator)
                .chain(this::createChannelAccountsBy)
                .chain(this::channelAccountsCreated));
    }

    private Uni<ChannelGeneratorEntity> selectChannelGenerator(List<ChannelGeneratorEntity> channelGeneratorCandidates) {
        var cids = channelGeneratorCandidates.stream().map(c -> c.id).toList();
        Log.debugf("cids = %s", cids);
        var selectedGenerators = channelGeneratorCandidates.stream().filter(c -> c.id % context.voteBuckets() == this.id).toList();

        if (!selectedGenerators.isEmpty()) {
            Log.infof("%s: Found %s channel generators to use for creating channel accounts.", taskId, selectedGenerators.size());
            Log.infof("%s: Using channel generator: %s", taskId, selectedGenerators.get(0).id);
            return Uni.createFrom().item(selectedGenerators.get(0));
        }

        Log.infof("%s: Not found any channel generators suitable for creating channel accounts in this task.", taskId);

        return Uni.createFrom().item(() -> null);
    }

    private Uni<ChannelAccountsCreatedPayload> createChannelAccountsBy(ChannelGeneratorEntity channelGeneratorEntity) {
        if(channelGeneratorEntity == null) {
            return Uni.createFrom().item(ChannelAccountsCreatedPayload.empty());
        }

        var isOnTestNet = channelGeneratorEntity.voting.isOnTestNetwork;
        var stellarOperations = context.stellarOperationsProducer().create(isOnTestNet);
        var payload = getStellarChannelAccountOperationPayload(channelGeneratorEntity);

        return stellarOperations.createChannelAccounts(payload)
                .map(accounts -> new ChannelAccountsCreatedPayload(accounts, channelGeneratorEntity.id));
    }

    private static StellarChannelAccountOperationPayload getStellarChannelAccountOperationPayload(ChannelGeneratorEntity channelGeneratorEntity) {
        var maxNumOfAccountsToCreate = StellarChannelBuilderTask.MAX_NUM_OF_ACCOUNTS_TO_CREATE_IN_ONE_BATCH;
        var accountsLeftToCreate = channelGeneratorEntity.accountsLeftToCreate;
        var numOfAccountsToActuallyCreate =
                accountsLeftToCreate >= maxNumOfAccountsToCreate ? maxNumOfAccountsToCreate : accountsLeftToCreate;

        return new StellarChannelAccountOperationPayload(
                channelGeneratorEntity.accountSecret,
                numOfAccountsToActuallyCreate,
                channelGeneratorEntity.voting.id
        );
    }

    private Uni<Void> channelAccountsCreated(ChannelAccountsCreatedPayload payload) {
        if(!payload.isEmpty()) {
            return context.channelAccountRepository().channelAccountsCreated(payload. accountsCreated, payload.channelGeneratorId);
        } else {
            return Uni.createFrom().item(() -> null);
        }
    }

    private record ChannelAccountsCreatedPayload(List<StellarChannelAccount> accountsCreated, Long channelGeneratorId) {
        static ChannelAccountsCreatedPayload empty() {
            return new ChannelAccountsCreatedPayload(Collections.emptyList(), -1L);
        }

        public boolean isEmpty() {
            return accountsCreated.isEmpty();
        }
    }
}
