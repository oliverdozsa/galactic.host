package host.galactic.data.repositories;

import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import static host.galactic.data.mappers.CreateVotingRequestMapper.from;

@ApplicationScoped
public class VotingRepository implements PanacheRepository<VotingEntity> {
    @WithTransaction
    public Uni<VotingEntity> createFrom(CreateVotingRequest createVotingRequest) {
        Log.info("createFrom()");
        Log.debugf("createFrom(): createVotingRequest = %s", createVotingRequest.toString());

        VotingEntity entity = from(createVotingRequest);
        return persist(entity);
    }

    public Uni<VotingEntity> getById(Long id) {
        Log.infof("getById(): id = %s", id);
        return findById(id);
    }
}
