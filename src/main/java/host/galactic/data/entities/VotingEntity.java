package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "created_by")
    public UserEntity createdBy;

    @Column(name = "user_given_funding_account_secret", columnDefinition = "text")
    public String userGivenFundingAccountSecret;

    @Column(name = "funding_account_secret", columnDefinition = "text")
    public String fundingAccountSecret;

    @Column(name = "asset_code", length = 20)
    public String assetCode;

    @Column(name = "is_on_test_network")
    public Boolean isOnTestNetwork;

    @OneToMany(mappedBy = "voting", cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    public List<VotingPollEntity> polls;

    @Column(name = "num_of_voters")
    public Integer numOfVoters;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "votings_partipicants",
            joinColumns = {@JoinColumn(name = "voting_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    public Set<UserEntity> voters;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.REMOVE)
    public List<ChannelGeneratorEntity> channelGenerators;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.REMOVE)
    public List<ChannelAccountEntity> channelAccounts;

    @Column(name = "distribution_account_secret", columnDefinition = "text")
    public String distributionAccountSecret;

    @Column(name = "ballot_account_secret", columnDefinition = "text")
    public String ballotAccountSecret;

    @Column(name = "issuer_account_secret", columnDefinition = "text")
    public String issuerAccountSecret;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.REMOVE)
    public List<EnvelopeSignatureEntity> signatures;
}
