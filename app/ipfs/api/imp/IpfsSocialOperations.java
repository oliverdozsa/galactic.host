package ipfs.api.imp;

import com.fasterxml.jackson.databind.JsonNode;
import crypto.AesCtrCrypto;
import executioncontexts.BlockchainExecutionContext;
import ipfs.api.IpfsApi;
import ipfs.data.social.IpfsActor;
import ipfs.data.social.IpfsEncryptedActor;
import play.libs.Json;


import javax.inject.Inject;
import java.util.Base64;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class IpfsSocialOperations {
    private final IpfsApi ipfsApi;
    private final BlockchainExecutionContext blockchainExecutionContext;

    @Inject
    public IpfsSocialOperations(IpfsApi ipfsApi, BlockchainExecutionContext blockchainExecContext) {
        this.ipfsApi = ipfsApi;
        this.blockchainExecutionContext = blockchainExecContext;
    }

    public CompletionStage<String> saveActor(IpfsActor ipfsActor, String encryptionKey) {
        return supplyAsync(() -> {
            String encryptedActorPayLoad = encryptActor(ipfsActor, encryptionKey);

            IpfsEncryptedActor ipfsEncryptedActor = new IpfsEncryptedActor();
            ipfsEncryptedActor.setPayload(encryptedActorPayLoad);

            return ipfsApi.saveJson(Json.toJson(ipfsEncryptedActor));

        }, blockchainExecutionContext);
    }

    private static String encryptActor(IpfsActor ipfsActor, String encryptionKey) {
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
        JsonNode ipfsActorAsJson = Json.toJson(ipfsActor);
        String ipfsActorJsonAsString = ipfsActorAsJson.toString();

        byte[] encryptedActorBytes = AesCtrCrypto.encrypt(keyBytes, ipfsActorJsonAsString.getBytes());
        return Base64.getEncoder().encodeToString(encryptedActorBytes);
    }
}
