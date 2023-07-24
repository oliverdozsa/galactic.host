package data.repositories.imp.social;

import data.entities.social.JpaActor;
import data.repositories.social.ActorRepository;
import exceptions.BusinessLogicViolationException;
import io.ebean.EbeanServer;
import play.Logger;
import requests.social.SignupRequest;

import javax.inject.Inject;
import java.util.Optional;

public class EbeanActorRepository implements ActorRepository {
    private static final Logger.ALogger logger = Logger.of(EbeanActorRepository.class);

    private final EbeanServer ebeanServer;

    @Inject
    public EbeanActorRepository(EbeanServer ebeanServer) {
        this.ebeanServer = ebeanServer;
    }

    @Override
    public JpaActor createFrom(SignupRequest request, String userEmail) {
        logger.info("createFrom(): request = {}, userEmail = {}", request, userEmail);

        Optional<JpaActor> existingActorMaybe = ebeanServer.createQuery(JpaActor.class)
                .where()
                .eq("email", userEmail)
                .findOneOrEmpty();

        if (existingActorMaybe.isPresent()) {
            throw new IllegalArgumentException("User with email =\"" + userEmail + "\" already exists!");
        }

        JpaActor actor = new JpaActor();
        actor.setEmail(userEmail);
        actor.setPreferredUserName(request.getPreferredUserName());
        actor.setAccountPublic(request.getAccountPublic());
        actor.setAccountSecret(request.getAccountSecret());
        actor.setUseTestnet(request.isUseTestnet());

        ebeanServer.save(actor);
        return actor;
    }
}
