package services.social;

import controllers.social.routes;
import crypto.AesCtrCrypto;
import data.entities.social.JpaActor;
import data.operations.social.ActorDbOperations;
import galactic.blockchain.operations.SocialBlockchainOperations;
import galactic.blockchain.api.Account;
import ipfs.api.imp.IpfsSocialOperations;
import ipfs.data.social.IpfsActor;
import play.Logger;
import play.mvc.Http;
import requests.social.SignupRequest;
import responses.social.ActorResponse;
import security.VerifiedJwt;

import javax.inject.Inject;
import java.util.Base64;
import java.util.concurrent.CompletionStage;

public class SocialService {
    private final SocialBlockchainOperations socialBlockchainOperations;
    private final ActorDbOperations actorDbOperations;
    private final IpfsSocialOperations ipfsSocialOperations;

    private static final Logger.ALogger logger = Logger.of(SocialService.class);

    @Inject
    public SocialService(SocialBlockchainOperations socialBlockchainOperations, ActorDbOperations actorDbOperations,
                         IpfsSocialOperations ipfsSocialOperations) {
        this.socialBlockchainOperations = socialBlockchainOperations;
        this.actorDbOperations = actorDbOperations;
        this.ipfsSocialOperations = ipfsSocialOperations;

    }

    public CompletionStage<String> signup(SignupRequest signupRequest, VerifiedJwt jwt) {
        logger.info("signup(): signupRequest = {}", signupRequest.toString());

        String encryptionKey = generateEncryptionKey();

        return ipfsSocialOperations.saveActor(IpfsActor.from(signupRequest), encryptionKey)
                .thenCompose(cid -> this.socialBlockchainOperations.signup(signupRequest, cid))
                .thenCompose(v -> this.actorDbOperations.createFrom(signupRequest, jwt.getEmail(), encryptionKey))
                .thenApply(JpaActor::getUserId);
    }

    public CompletionStage<ActorResponse> getActor(String userId, Http.Request request) {
        logger.info("getActor(): userId = {}", userId);

        return actorDbOperations.getByUserId(userId)
                .thenApply(this::getJpaActorIntoComposedActor)
                .thenCompose(this::getProfiledCidIntoComposedActor)
                .thenCompose(this::getIpfsActorIntoComposedActor)
                .thenApply(c -> fromComposedActor(c, request));
    }

    private ComposedActor getJpaActorIntoComposedActor(JpaActor jpaActor) {
        ComposedActor composedActor = new ComposedActor();
        composedActor.jpaActor = jpaActor;
        return composedActor;
    }

    private CompletionStage<ComposedActor> getProfiledCidIntoComposedActor(ComposedActor c) {
        Account forAccount = getAccountOf(c.jpaActor);
        return socialBlockchainOperations.getProfileCid(forAccount, c.jpaActor.getNetwork(), c.jpaActor.isUseTestnet())
                .thenApply(cid -> {
                    c.actorCid = cid;
                    return c;
                });
    }

    private CompletionStage<ComposedActor> getIpfsActorIntoComposedActor(ComposedActor c) {
        return ipfsSocialOperations.getActor(c.actorCid, c.jpaActor.getEncryptionKey())
                .thenApply(i -> {
                    c.ipfsActor = i;
                    return c;
                });
    }

    private static ActorResponse fromComposedActor(ComposedActor composedActor, Http.Request request) {
        JpaActor jpaActor = composedActor.jpaActor;

        ActorResponse actorResponse = new ActorResponse();
        actorResponse.setId(routes.SocialController.getActor(jpaActor.getUserId()).absoluteURL(request));
        actorResponse.setFollowing(routes.SocialController.getFollowingOf(jpaActor.getUserId()).absoluteURL(request));
        actorResponse.setFollowers(routes.SocialController.getFollowersOf(jpaActor.getUserId()).absoluteURL(request));
        actorResponse.setLiked(routes.SocialController.getLikedOf(jpaActor.getUserId()).absoluteURL(request));
        actorResponse.setInbox(routes.SocialController.getInboxOf(jpaActor.getUserId()).absoluteURL(request));
        actorResponse.setOutbox(routes.SocialController.getOutboxOf(jpaActor.getUserId()).absoluteURL(request));

        IpfsActor ipfsActor = composedActor.ipfsActor;
        actorResponse.setName(ipfsActor.getName());
        actorResponse.setPreferredUsername(ipfsActor.getPreferredUsername());

        return actorResponse;
    }

    private static String generateEncryptionKey() {
        byte[] keyBytes = AesCtrCrypto.generateKey();
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    private static Account getAccountOf(JpaActor jpaActor) {
        return new Account(jpaActor.getAccountSecret(), jpaActor.getAccountPublic());
    }

    private static class ComposedActor {
        public JpaActor jpaActor;
        public IpfsActor ipfsActor;
        public String actorCid;
    }
}
