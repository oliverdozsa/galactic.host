package host.galactic.data.repositories;

import host.galactic.data.entities.VotingEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VotingRepository implements PanacheRepository<VotingEntity> {
}
