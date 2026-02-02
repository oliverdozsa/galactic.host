package host.galactic.data.repositories;

import host.galactic.data.entities.VoterTransactionEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VoterTransactionRepository implements PanacheRepository<VoterTransactionEntity> {
    public Uni<VoterTransactionEntity> createFrom(String signature, String transaction) {
        var entity = new VoterTransactionEntity();
        entity.signature = signature;
        entity.transaction = transaction;

        return persist(entity);
    }

    public Uni<VoterTransactionEntity> findBySignature(String signature) {
        return find("signature = ?1", signature)
                .firstResult();
    }
}
