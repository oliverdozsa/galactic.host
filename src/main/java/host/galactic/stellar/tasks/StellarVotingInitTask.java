package host.galactic.stellar.tasks;

import host.galactic.data.repositories.ChannelGeneratorRepository;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;

import java.util.function.Function;

public class StellarVotingInitTask implements Function<ScheduledExecution, Uni<Void>> {
    private ChannelGeneratorRepository repository;

    StellarVotingInitTask(ChannelGeneratorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Uni<Void> apply(ScheduledExecution execution) {
        Log.infof("%s", execution.getTrigger().getId());
        return Uni.createFrom().item(() -> null);
    }
}
