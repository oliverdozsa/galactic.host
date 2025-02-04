package host.galactic.data.entities;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {
    @Id
    @Column(name = "id")
    public String id;

    @Column(name = "email")
    public String email;

    @ManyToMany
    @JoinTable(
            name = "votings_partipicants",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "voting_id")}
    )
    public List<VotingEntity> votings;
}
