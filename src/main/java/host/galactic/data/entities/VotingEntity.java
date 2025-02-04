package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "voting")
public class VotingEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "title", columnDefinition = "text")
    public String title;

    @Column(name = "description", columnDefinition = "text")
    public String description;

    @Column(name = "max_voters", nullable = false)
    public Integer maxVoters;

    @Column(name = "created_at", nullable = false)
    public Instant createdAt;

    @Column(name = "encryption_key", columnDefinition = "text")
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

    @Column(name = "user_given_funding_account_secret", columnDefinition = "text")
    public String userGivenFundingAccountSecret;

    @Column(name = "asset_code", length = 20)
    public String assetCode;

    @Column(name = "is_on_test_network")
    public Boolean isOnTestNetwork;

    @OneToMany(mappedBy = "voting", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    public List<VotingPollEntity> polls;

    @ManyToMany(mappedBy = "votings")
    public List<UserEntity> voters;
}
