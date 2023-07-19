package data.repositories.imp.social;

import data.entities.social.JpaActor;
import data.repositories.social.ActorRepository;
import io.ebean.EbeanServer;
import play.Logger;
import requests.social.SignupRequest;

import javax.inject.Inject;

public class EbeanActorRepository implements ActorRepository {
    private static final Logger.ALogger logger = Logger.of(EbeanActorRepository.class);

    private final EbeanServer ebeanServer;

    @Inject
    public EbeanActorRepository(EbeanServer ebeanServer) {
        this.ebeanServer = ebeanServer;
    }

    @Override
    public JpaActor createFrom(SignupRequest request, String userId) {
        // TODO
        return null;
    }
}
