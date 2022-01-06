package devote.blockchain.api;

public interface VoterAccount {
    String create(CreationData creationData);

    class CreationData {
        public String channelSecret;
        public KeyPair distributionKeyPair;
        public String voterPublicKey;
        public String issuerPublicKey;
        public String assetCode;
        public Long votesCap;
    }
}