package host.galactic.stellar.tasks;

import io.quarkus.logging.Log;
import io.quarkus.scheduler.ScheduledExecution;

import java.util.function.Consumer;

public class StellarChannelBuilderTask implements Consumer<ScheduledExecution> {
    @Override
    public void accept(ScheduledExecution execution) {
        Log.infof("id = %s", execution.getTrigger().getId());
    }
}
