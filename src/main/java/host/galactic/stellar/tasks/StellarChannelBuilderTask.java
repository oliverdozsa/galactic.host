package host.galactic.stellar.tasks;

import host.galactic.data.entities.ChannelGeneratorEntity;
import host.galactic.stellar.operations.StellarChannelAccount;
import host.galactic.stellar.operations.StellarChannelAccountOperationPayload;
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
        var selectedGenerators = channelGeneratorCandidates.stream().filter(c -> c.id % context.voteBuckets() == this.id).toList();

        if (!selectedGenerators.isEmpty()) {
            return Uni.createFrom().item(selectedGenerators.get(0));
        }

        return Uni.createFrom().item(() -> null);
    }

    private Uni<List<StellarChannelAccount>> createChannelAccountsBy(ChannelGeneratorEntity channelGeneratorEntity) {
        if(channelGeneratorEntity == null) {
            return Uni.createFrom().item(Collections::emptyList);
        }

        var isOnTestNet = channelGeneratorEntity.voting.isOnTestNetwork;
        var stellarOperations = context.stellarOperationsProducer().create(isOnTestNet);

        var maxNumOfAccountsToCreate = StellarChannelBuilderTask.MAX_NUM_OF_ACCOUNTS_TO_CREATE_IN_ONE_BATCH;
        var accountsLeftToCreate = channelGeneratorEntity.accountsLeftToCreate;
        var numOfAccountsToActuallyCreate =
                accountsLeftToCreate >= maxNumOfAccountsToCreate ? maxNumOfAccountsToCreate : accountsLeftToCreate;

        var payload = new StellarChannelAccountOperationPayload(
                channelGeneratorEntity.accountSecret,
                numOfAccountsToActuallyCreate,
                channelGeneratorEntity.voting.id
        );

        return stellarOperations.createChannelAccounts(payload);
    }

    private Uni<Void> channelAccountsCreated(List<StellarChannelAccount> accountsCreated) {
        if(!accountsCreated.isEmpty()) {
            return context.channelAccountRepository().channelAccountsCreated(accountsCreated);
        } else {
            return Uni.createFrom().item(() -> null);
        }
    }
}
