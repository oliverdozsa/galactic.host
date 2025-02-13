package host.galactic.data.repositories;

import host.galactic.data.entities.UserEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;

import java.util.List;

@ApplicationScoped
public class UserRepository implements PanacheRepository<UserEntity> {
    @WithTransaction
    public Uni<UserEntity> createIfNotExists(String email) {
        Log.infof("createIfNotExists(): email = %s", email);
        return find("email = ?1", email)
                .singleResult()
                .onFailure(NoResultException.class)
                .recoverWithUni(() -> createWithEmail(email));
    }

    public Uni<List<UserEntity>> createIfNotExist(List<String> emails) {
        Log.infof("createIfNotExist(): emails = %s", emails);
        return find("where email in ?1", emails)
                .list()
                .onItem()
                .transformToUni(dbUsers -> persistEmailsNotAlreadyInDb(dbUsers, emails));
    }

    public Uni<UserEntity> findByEmail(String email) {
        Log.infof("findByEmail(): email = %s", email);
        return find("email = ?1", email)
                .singleResult();
    }

    private Uni<UserEntity> createWithEmail(String email) {
        Log.infof("createWithEmail(): email = %s", email);
        UserEntity userEntity = new UserEntity();
        userEntity.email = email;
        return persistAndFlush(userEntity);
    }

    private Uni<List<UserEntity>> persistEmailsNotAlreadyInDb(List<UserEntity> usersInDb, List<String> allEmailsToPersist) {
        List<String> dbEmails = usersInDb.stream().map(u -> u.email).toList();
        List<String> emailsNotInDb = allEmailsToPersist.stream().filter(e -> !dbEmails.contains(e))
                .toList();
        Log.debugf("persistEmailsNotAlreadyInDb(): email not in DB: %s", emailsNotInDb);
        List<UserEntity> entitiesToPersist = emailsNotInDb.stream()
                .map(e -> {
                    UserEntity user = new UserEntity();
                    user.email = e;
                    return user;
                }).toList();
        return persist(entitiesToPersist)
                .onItem()
                .transformToUni(v -> find("where email in ?1", allEmailsToPersist).list());

    }
}
