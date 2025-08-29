package host.galactic.stellar.tasks;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;
import io.smallrye.mutiny.Uni;

import java.util.function.Function;

public class StellarChannelBuilderTask implements Function<ScheduledExecution, Uni<Void>> {
    private int id;

    StellarChannelBuilderTask(int id) {
        this.id = id;
    }

    @Override
    public Uni<Void> apply(ScheduledExecution execution) {
        Log.infof("%s", execution.getTrigger().getId());
        return Uni.createFrom().item(() -> null);
    }
}
