package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "channel_generator")
public class ChannelGeneratorEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "account_secret", columnDefinition = "text")
    public String accountSecret;

    @Column(name = "max_voters")
    public Integer maxVoters;

    @Column(name = "is_refunded")
    public boolean isRefunded;

    @ManyToOne
    @JoinColumn(name = "voting_id")
    public VotingEntity voting;
}
