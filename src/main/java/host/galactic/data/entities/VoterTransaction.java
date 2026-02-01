package host.galactic.data.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "voter_transaction")
public class VoterTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Long id;

    @Column(name = "signature", unique = true, columnDefinition = "text")
    public String signature;

    @Column(name = "transaction", columnDefinition = "text")
    public String transaction;
}
