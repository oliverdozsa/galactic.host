package data.entities.social;

import javax.persistence.*;

@Entity
@Table(name = "actor")
public class JpaActor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @Lob
    @Column(name = "encryption_key", nullable = false)
    private String encryptionKey;

    @Lob
    @Column(name = "account_public", nullable = false)
    private String accountPublic;

    @Lob
    @Column(name = "account_secret", nullable = false)
    private String accountSecret;

    @Column(name = "use_testnet")
    private boolean useTestnet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccountPublic() {
        return accountPublic;
    }

    public void setAccountPublic(String accountPublic) {
        this.accountPublic = accountPublic;
    }

    public String getAccountSecret() {
        return accountSecret;
    }

    public void setAccountSecret(String accountSecret) {
        this.accountSecret = accountSecret;
    }

    public boolean isUseTestnet() {
        return useTestnet;
    }

    public void setUseTestnet(boolean useTestnet) {
        this.useTestnet = useTestnet;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
