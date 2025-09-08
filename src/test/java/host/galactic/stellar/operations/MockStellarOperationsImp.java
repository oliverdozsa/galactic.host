package host.galactic.stellar.operations;

import io.smallrye.mutiny.Uni;
import org.stellar.sdk.KeyPair;

import java.util.ArrayList;
import java.util.List;

class MockStellarOperationsImp implements StellarOperations {
    private int voteBuckets;

    public MockStellarOperationsImp(int voteBuckets) {
        this.voteBuckets = voteBuckets;
    }

    @Override
    public Uni<Void> transferXlmFrom(String sourceAccountSecret, double xlm, String targetAccountSecret) {
        if (MockStellarOperations.transferXlmShouldSucceed) {
            return Uni.createFrom().voidItem();
        } else {
            var exception = new StellarOperationsException("Mock failure has been thrown to test failure of transferXlmFrom()!");
            return Uni.createFrom().failure(exception);
        }
    }

    @Override
    public Uni<List<StellarChannelGenerator>> createChannelGenerators(StellarChannelGeneratorOperationPayload payload) {
        List<StellarChannelGenerator> channelGenerators = new ArrayList<>(payload.numOfGeneratorsToCreate());

        int numOfVotersPerChannelGen = payload.maxVoters() / payload.numOfGeneratorsToCreate();
        int remainingVoters = payload.maxVoters() % payload.numOfGeneratorsToCreate();

        for(int i = 0; i < payload.numOfGeneratorsToCreate() - 1; i++) {
            KeyPair account = KeyPair.random();
            channelGenerators.add(new StellarChannelGenerator(new String(account.getSecretSeed()), numOfVotersPerChannelGen, payload.votingId()));
        }

        channelGenerators.add(new StellarChannelGenerator(payload.fundingAccountSecret(), numOfVotersPerChannelGen + remainingVoters, payload.votingId()));

        return Uni.createFrom().item(channelGenerators);
    }
}
