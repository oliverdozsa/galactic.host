package host.galactic.stellar.tasks;

import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class StellarTasks {
    @Inject
    Scheduler scheduler;

    private static int channelBuilderTasks = 0;

    @Startup
    public void init() {
        addChannelBuilderTask();
    }

    private void addChannelBuilderTask() {
        scheduler.newJob("stellar-channel-builder-" + (++StellarTasks.channelBuilderTasks))
                .setDelayed("5s")
                .setInterval("7s")
                .setTask(new StellarChannelBuilderTask())
                .schedule();
    }
}
