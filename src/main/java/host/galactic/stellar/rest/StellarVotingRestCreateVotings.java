package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.UserRepository;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.operations.StellarOperations;
import host.galactic.stellar.operations.StellarOperationsProducer;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.oidc.UserInfo;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.stellar.sdk.KeyPair;

import java.net.URI;

@RequestScoped
class StellarVotingRestCreateVotings {
    @Inject
    VotingRepository votingRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    StellarOperationsProducer stellarOperationsProducer;

    @Inject
    UserInfo userInfo;

    @WithTransaction
    public Uni<Response> create(CreateVotingRequest createVotingRequest) {
        Log.info("create(): Got request to create a voting.");
        Log.debugf("create(): Details of voting request: user = \"%s\", createVotingRequest = %s", userInfo.getEmail(), createVotingRequest.toString());

        KeyPair internalFundingAccount = KeyPair.random();
        var internalFundingAccountSecret = new String(internalFundingAccount.getSecretSeed());

        return deductEstimatedCost(createVotingRequest, internalFundingAccount)
                .chain(v -> userRepository.findByEmail(userInfo.getEmail()))
                .chain(u -> votingRepository.createFrom(createVotingRequest, u, internalFundingAccountSecret))
                .map(StellarVotingRestCreateVotings::toCreatedResponse)
                .onFailure()
                .invoke(t -> Log.warn("create(): Could not create voting!", t));
    }

    private Uni<Void> deductEstimatedCost(CreateVotingRequest request, KeyPair internalFundingAccount) {
        StellarOperations stellarOperations = stellarOperationsProducer.create(request.useTestNet());
        String internalFundingAccountSecret = new String(internalFundingAccount.getSecretSeed());
        double estimatedCost = request.maxVoters() * 4 + 110;

        return stellarOperations.createInternalFunding(request.fundingAccountSecret(), estimatedCost, internalFundingAccountSecret);
    }

    private static Response toCreatedResponse(VotingEntity entity) {
        URI entityId = UriBuilder
                .fromResource(StellarVotingRest.class)
                .path("/{id}")
                .build(entity.id);
        return Response.created(entityId).build();
    }
}
