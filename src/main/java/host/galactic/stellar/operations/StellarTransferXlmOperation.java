package host.galactic.stellar.operations;

import org.stellar.sdk.Network;
import org.stellar.sdk.Server;

class StellarTransferXlmOperation {
    private Server server;
    private Network network;

    StellarTransferXlmOperation(Server server, Network network) {
        this.server = server;
        this.network = network;
    }
}
