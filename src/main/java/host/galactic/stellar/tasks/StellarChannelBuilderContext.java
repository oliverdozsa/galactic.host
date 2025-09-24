package host.galactic.stellar.tasks;

import host.galactic.data.repositories.ChannelAccountRepository;
import host.galactic.data.repositories.ChannelGeneratorRepository;
import host.galactic.stellar.operations.StellarOperations;
import host.galactic.stellar.operations.StellarOperationsProducer;
import org.hibernate.reactive.mutiny.Mutiny;

public record StellarChannelBuilderContext(
        int voteBuckets,
        ChannelGeneratorRepository channelGeneratorRepository,
        ChannelAccountRepository channelAccountRepository,
        Mutiny.SessionFactory sessionFactory,
        StellarOperationsProducer stellarOperationsProducer
) {
}
