package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "voting")
public class VotingEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "title")
    @Lob
    public String title;

    @Column(name = "description")
    @Lob
    public String description;

    @Column(name = "max_voters", nullable = false)
    public Long maxVoters;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "encryption_key")
    @Lob
    public String encryptionKey;

    @Column(name = "encrypted_until")
    public Instant encryptedUntil;

    @Column(name = "start_date", nullable = false)
    public Instant startDate;

    @Column(name = "end_date", nullable = false)
    public Instant endDate;

    @Column(name = "ballot_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    public BallotType ballotType;

    @Column(name = "max_choices")
    public Integer maxChoices;

    @Column(name = "visibility", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    public Visibility visibility;

    @Column(name = "created_by", nullable = false)
    public String createdBy;
}
