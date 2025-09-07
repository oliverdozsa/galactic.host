package host.galactic.stellar.tasks;

import host.galactic.data.repositories.ChannelGeneratorRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.operations.StellarOperationsProducer;
import org.hibernate.reactive.mutiny.Mutiny;

public record StellarVotingInitContext(VotingRepository votingRepository,
                                       ChannelGeneratorRepository channelGeneratorRepository,
                                       StellarOperationsProducer operationsProducer, String fundingAccountSecret,
                                       Mutiny.SessionFactory sessionFactory) {
}
