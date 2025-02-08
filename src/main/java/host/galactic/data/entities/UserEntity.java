package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "email", unique = true)
    public String email;

    @OneToMany(mappedBy = "createdBy")
    public List<VotingEntity> createdVotings;

    @ManyToMany
    @JoinTable(
            name = "votings_partipicants",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "voting_id")}
    )
    public List<VotingEntity> votings;
}
