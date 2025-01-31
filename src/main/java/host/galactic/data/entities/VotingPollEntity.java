package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "voting_poll")
public class VotingPollEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "index", nullable = false)
    public Integer index;

    @Column(name = "question", nullable = false, columnDefinition = "text")
    public String question;

    @Column(name = "description", columnDefinition = "text")
    public String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voting_id")
    public VotingEntity voting;

    @OneToMany(mappedBy = "poll", cascade = {CascadeType.MERGE, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    public List<VotingPollOptionEntity> options;
}
