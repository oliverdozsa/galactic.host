package host.galactic.data.repositories;

import host.galactic.data.entities.UserEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {
    @WithTransaction
    public Uni<UserEntity> createIfNotExists(String email) {
        return find("email = ?1", email)
                .singleResult()
                .onFailure(NoResultException.class)
                .recoverWithUni(() -> createWithEmail(email));
    }

    private Uni<UserEntity> createWithEmail(String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.email = email;
        return persist(userEntity);
    }
}
