package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "envelope_signature")
public class EnvelopeSignatureEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "signature", columnDefinition = "text")
    public String signature;

    @ManyToOne
    public VotingEntity voting;

    @ManyToOne
    public UserEntity user;
}
