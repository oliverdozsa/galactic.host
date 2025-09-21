package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "channel_account")
public class ChannelAccountEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "account_secret", columnDefinition = "text")
    public String accountSecret;

    @Column(name = "is_consumed")
    public boolean isConsumed;

    @Column(name = "is_refunded")
    public boolean isRefunded;

    @ManyToOne
    @JoinColumn(name = "voting_id")
    public VotingEntity voting;
}
