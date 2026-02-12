package host.galactic.stellar.rest;

import host.galactic.data.entities.VotingEntity;
import host.galactic.data.repositories.VotingRepository;
import host.galactic.stellar.encryption.EncryptedChoice;
import host.galactic.stellar.rest.requests.voting.VotingEncryptChoiceRequest;
import host.galactic.stellar.rest.responses.voting.VotingEncryptChoiceResponse;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;

@RequestScoped
public class StellarVotingRestEncryptedChoice {
    @Inject
    VotingRepository votingRepository;

    @WithSession
    public Uni<VotingEncryptChoiceResponse> encrypt(Long votingId, VotingEncryptChoiceRequest request) {
        Log.infof("Got request to encrypt a choice for voting = %s", votingId);
        return votingRepository.getById(votingId)
                .onItem().invoke(this::assertVotingIsEncrypted)
                .map(e -> EncryptedChoice.encryptToBase64(request.choice(), e.encryptionKey))
                .map(VotingEncryptChoiceResponse::new);
    }

    private void assertVotingIsEncrypted(VotingEntity voting) {
        if(voting.encryptedUntil == null) {
            throw new BadRequestException();
        }
    }
}
