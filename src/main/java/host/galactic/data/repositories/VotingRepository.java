package host.galactic.data.repositories;

import host.galactic.data.entities.UserEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.requests.voting.AddVotersRequest;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.List;

import static host.galactic.data.mappers.CreateVotingRequestMapper.from;

@ApplicationScoped
public class VotingRepository implements PanacheRepository<VotingEntity> {
    @WithTransaction
    public Uni<VotingEntity> createFrom(CreateVotingRequest createVotingRequest, UserEntity user) {
        Log.info("createFrom(): Creating a voting entity.");
        Log.debugf("createFrom(): Details of voting entity to be created: user.email = \"%s\", createVotingRequest = %s", user.email, createVotingRequest.toString());

        VotingEntity entity = from(createVotingRequest, user);
        return persist(entity);
    }

    public Uni<VotingEntity> getById(Long id) {
        Log.infof("getById(): Getting voting entity by id = %s", id);
        return findById(id);
    }

    @WithTransaction
    public Uni<VotingEntity> addVotersTo(Long votingId, List<UserEntity> voters) {
        Log.info("addVotersByEmailTo()");
        Log.debugf("addVotersTo(): votingId = %s, voters = %s", voters);

        return findById(votingId)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException())
                .onItem()
                .call(v -> Mutiny.fetch(v.voters))
                .invoke(v -> {
                    v.voters.addAll(voters);
                });
    }
}
