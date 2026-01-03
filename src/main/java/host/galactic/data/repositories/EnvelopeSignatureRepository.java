package host.galactic.data.repositories;

import host.galactic.data.entities.EnvelopeSignatureEntity;
import host.galactic.data.entities.UserEntity;
import host.galactic.data.entities.VotingEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EnvelopeSignatureRepository implements PanacheRepository<EnvelopeSignatureEntity> {
    @Inject
    private UserRepository userRepository;

    public Uni<EnvelopeSignatureEntity> findFor(Long votingId, String email) {
        return find("voting.id = ?1 and user.email = ?2", votingId, email)
                .singleResult();
    }

    @WithTransaction
    public Uni<EnvelopeSignatureEntity> create(String signature, Long votingId, String userEmail) {
        return userRepository.findByEmail(userEmail)
                .onItem()
                .transformToUni(user -> create(signature, votingId, user.id));
    }

    private Uni<EnvelopeSignatureEntity> create(String signature, Long votingId, Long userId) {
        var entity = new EnvelopeSignatureEntity();
        entity.signature = signature;

        entity.voting = new VotingEntity();
        entity.voting.id = votingId;

        entity.user = new UserEntity();
        entity.user.id = userId;

        return persist(entity);
    }
}
