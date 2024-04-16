package ipfs.api.imp;

import ipfs.api.IpfsApi;
import ipfs.data.social.IpfsActor;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

import static java.util.concurrent.CompletableFuture.completedFuture;


public class IpfsSocialOperations {
    private final IpfsApi ipfsApi;

    @Inject
    public IpfsSocialOperations(IpfsApi ipfsApi) {
        this.ipfsApi = ipfsApi;
    }

    public CompletionStage<String> saveActor(IpfsActor ipfsActor, String encryptionKey) {
        // TODO
        return completedFuture("");
    }
}
