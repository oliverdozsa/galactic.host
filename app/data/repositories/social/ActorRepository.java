package data.repositories.social;

import data.entities.social.JpaActor;
import requests.social.SignupRequest;

public interface ActorRepository {
    JpaActor createFrom(SignupRequest request, String userEmail);
}
