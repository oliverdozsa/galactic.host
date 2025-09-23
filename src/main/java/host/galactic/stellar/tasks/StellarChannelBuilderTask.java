package host.galactic.stellar.tasks;

import host.galactic.data.entities.ChannelGeneratorEntity;
import host.galactic.stellar.operations.StellarChannelAccount;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;

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
        // TODO
        return null;
    }

    private Uni<List<StellarChannelAccount>> createChannelAccountsBy(ChannelGeneratorEntity channelGeneratorEntity) {
        // TODO
        return null;
    }

    private Uni<Void> channelAccountsCreated(List<StellarChannelAccount> accountsCreated) {
        // TODO
        return null;
    }
}
