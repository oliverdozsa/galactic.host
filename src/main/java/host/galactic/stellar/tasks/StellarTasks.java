package host.galactic.stellar.tasks;

import host.galactic.data.repositories.ChannelGeneratorRepository;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class StellarTasks {
    @Inject
    Scheduler scheduler;

    @Inject
    ChannelGeneratorRepository channelGeneratorRepository;

    @ConfigProperty(name = "galactic.host.vote.buckets")
    private Integer voteBuckets;

    @Startup
    public void init() {
        Log.infof("Creating %d channel builder tasks.", voteBuckets);
        for(int i = 0; i < voteBuckets; i++) {
            addChannelBuilderTask(i);
            addVotingInitTask(i);
        }
    }

    private void addChannelBuilderTask(int id) {
        scheduler.newJob("stellar-channel-builder-" + id)
                .setDelayed("5s")
                .setInterval("7s")
                .setAsyncTask(new StellarChannelBuilderTask(id))
                .schedule();
    }

    private void addVotingInitTask(int id) {
        scheduler.newJob("stellar-voting-init-" + id)
                .setDelayed("3s")
                .setInterval("5s")
                .setAsyncTask(new StellarVotingInitTask(channelGeneratorRepository))
                .schedule();
    }
}
