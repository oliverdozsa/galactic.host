package host.galactic.data.repositories;

import host.galactic.data.entities.UserEntity;
import host.galactic.data.entities.VotingEntity;
import host.galactic.stellar.rest.requests.voting.CreateVotingRequest;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Page;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.hibernate.reactive.mutiny.Mutiny;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static host.galactic.data.mappers.CreateVotingRequestMapper.from;

@ApplicationScoped
public class VotingRepository implements PanacheRepository<VotingEntity> {
    @WithTransaction
    public Uni<VotingEntity> createFrom(CreateVotingRequest createVotingRequest, UserEntity user, String internalFundingAccountSecret) {
        Log.info("createFrom(): Creating a voting entity.");
        Log.debugf("createFrom(): Details of voting entity to be created: user.email = \"%s\", createVotingRequest = %s", user.email, createVotingRequest.toString());

        var entity = from(createVotingRequest, user, internalFundingAccountSecret);
        return persist(entity);
    }

    public Uni<VotingEntity> getById(Long id) {
        Log.infof("getById(): Getting voting entity by id = %s", id);
        return findAndAssertById(id);
    }

    @WithTransaction
    public Uni<VotingEntity> addVotersTo(Long votingId, List<UserEntity> voters) {
        Log.info("addVotersTo(): About to add voters to a voting.");
        Log.debugf("addVotersTo(): Details: votingId = %s, voters = %s", votingId, voters);

        return findAndAssertById(votingId)
                .onItem()
                .call(v -> Mutiny.fetch(v.voters))
                .invoke(v -> {
                    checkIfMaxVotersWouldBeExceeded(v, voters);
                    v.voters.addAll(voters);
                    v.numOfVoters = v.voters.size();
                });
    }

    public Uni<host.galactic.data.utils.Page<VotingEntity>> getVotingsOfUser(String email, int page) {
        var query = find("createdBy.email", email)
                .page(Page.of(page, 15));

        var listUni = query.list();
        var totalPagesUni = query.pageCount();
        return Uni.combine().all().unis(listUni, totalPagesUni).asTuple()
                .map(t -> new host.galactic.data.utils.Page<>(t.getItem1(), t.getItem2()));
    }

    public Uni<host.galactic.data.utils.Page<VotingEntity>> getVotingsOfVoter(String email, int page) {
        Log.infof("getVotingsOfVoter(): Getting %s's votings as voter from DB at page: %s", email, page);
        var query = find("select v from VotingEntity v join v.voters u where u.email = ?1", email)
                .page(Page.of(page, 15));

        var listUni = query.list();
        var totalPagesUni = query.pageCount();
        return Uni.combine().all().unis(listUni, totalPagesUni).asTuple()
                .map(t -> new host.galactic.data.utils.Page<>(t.getItem1(), t.getItem2()));
    }

    @WithTransaction
    public Uni<Void> deleteById(long id, String email) {
        return findAndAssertById(id)
                .onItem()
                .call(v -> Mutiny.fetch(v.createdBy))
                .invoke(v -> {
                    if(!v.createdBy.email.equals(email)) {
                        throw new ForbiddenException("User " + email + " is not the creator of voting with id =" + id + ".");
                    }
                })
                .chain(v -> deleteById(v.id))
                .map(b -> null);
    }

    public Uni<VotingEntity> getAnUninitializedVoting() {
        Log.debug("getAnUninitializedVoting(): Finding an uninitialized voting.");
        return find("select v from VotingEntity v left join v.channelGenerators cg where cg.voting is null").firstResult();
    }

    private void checkIfMaxVotersWouldBeExceeded(VotingEntity voting, List<UserEntity> usersToAdd){
        var usersToAddAsSet = new HashSet<>(usersToAdd);
        usersToAddAsSet.addAll(voting.voters);
        if(usersToAddAsSet.size() > voting.maxVoters) {
            Log.warnf("checkIfMaxVotersWouldBeExceeded(): Can't add voters to voting \"%s\" as maximum voters would be exceeded.", voting.id);
            throw new BadRequestException("Can't add voters to voting \"" + voting.id + "\" as max. voters would be exceeded.");
        }
    }

    private Uni<VotingEntity> findAndAssertById(long id) {
        return findById(id)
                .onItem()
                .ifNull()
                .failWith(new NotFoundException());
    }
}
