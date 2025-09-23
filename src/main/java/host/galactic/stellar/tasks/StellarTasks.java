package host.galactic.stellar.tasks;

import host.galactic.data.repositories.ChannelAccountRepository;
import host.galactic.data.repositories.ChannelGeneratorRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.operations.StellarOperationsProducer;
import io.quarkus.logging.Log;
import io.quarkus.runtime.Startup;
import io.quarkus.scheduler.Scheduler;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.reactive.mutiny.Mutiny;

@ApplicationScoped
public class StellarTasks {
    @Inject
    Scheduler scheduler;

    @Inject
    ChannelGeneratorRepository channelGeneratorRepository;

    @Inject
    VotingRepository votingRepository;

    @Inject
    ChannelAccountRepository channelAccountRepository;

    @Inject
    StellarOperationsProducer stellarOperationsProducer;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @ConfigProperty(name = "galactic.host.vote.buckets")
    private Integer voteBuckets;

    @ConfigProperty(name = "galactic.host.voting.init.task.delay")
    private String votingInitTaskDelay;

    @ConfigProperty(name = "galactic.host.voting.init.task.interval")
    private String votingInitTaskInterval;

    @ConfigProperty(name = "galactic.host.channel.task.delay")
    private String channelTaskDelay;

    @ConfigProperty(name = "galactic.host.channel.task.interval")
    private String channelTaskInterval;

    @Startup
    public void init() {
        Log.infof("Creating %d channel builder tasks.", voteBuckets);
        for (int i = 0; i < voteBuckets; i++) {
            addChannelBuilderTask(i);
        }

        addVotingInitTask(0);
    }

    private void addChannelBuilderTask(int id) {
        var context = new StellarChannelBuilderContext(voteBuckets, channelGeneratorRepository,
                channelAccountRepository, sessionFactory
        );

        scheduler.newJob("stellar-channel-builder-" + id)
                .setDelayed(channelTaskDelay)
                .setInterval(channelTaskInterval)
                .setAsyncTask(new StellarChannelBuilderTask(id, context))
                .schedule();
    }

    private void addVotingInitTask(int id) {
        var context = new StellarVotingInitContext(
                votingRepository, channelGeneratorRepository, stellarOperationsProducer,
                sessionFactory, voteBuckets
        );

        scheduler.newJob("stellar-voting-init-" + id)
                .setDelayed(votingInitTaskDelay)
                .setInterval(votingInitTaskInterval)
                .setAsyncTask(new StellarVotingInitTask(context))
                .schedule();
    }
}
