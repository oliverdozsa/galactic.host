package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "poll_option")
public class VotingPollOptionEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "name", nullable = false)
    @Lob
    public String name;

    @Column(name = "code", nullable = false)
    public Integer code;

    @ManyToOne(optional = false)
    @JoinColumn(name = "poll_id")
    public VotingPollEntity poll;
}
