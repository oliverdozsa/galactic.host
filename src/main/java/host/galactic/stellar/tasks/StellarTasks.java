package host.galactic.stellar.tasks;

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
    StellarOperationsProducer stellarOperationsProducer;

    @Inject
    Mutiny.SessionFactory sessionFactory;

    @ConfigProperty(name = "galactic.host.vote.buckets")
    private Integer voteBuckets;

    @ConfigProperty(name = "galactic.pub.internal.funding.account.secret")
    private String internalFundingAccountSecret;

    @Startup
    public void init() {
        Log.infof("Creating %d channel builder tasks.", voteBuckets);
        for (int i = 0; i < voteBuckets; i++) {
            addChannelBuilderTask(i);
        }

        addVotingInitTask(0);
    }

    private void addChannelBuilderTask(int id) {
        scheduler.newJob("stellar-channel-builder-" + id)
                .setDelayed("5s")
                .setInterval("7s")
                .setAsyncTask(new StellarChannelBuilderTask(id))

                .schedule();
    }

    private void addVotingInitTask(int id) {
        StellarVotingInitContext context = new StellarVotingInitContext(
                votingRepository, channelGeneratorRepository, stellarOperationsProducer,
                internalFundingAccountSecret, sessionFactory, voteBuckets
        );

        scheduler.newJob("stellar-voting-init-" + id)
                .setDelayed("3s")
                .setInterval("5s")
                .setAsyncTask(new StellarVotingInitTask(context))
                .schedule();
    }
}
