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

    @Lob
    @Column(name = "inbox")
    private String inbox;

    @Lob
    @Column(name = "outbox")
    private String outbox;

    @Lob
    @Column(name = "following")
    private String following;

    @Lob
    @Column(name = "followers")
    private String followers;

    @Lob
    @Column(name = "liked")
    private String liked;

    @Lob
    @Column(name = "preferred_user_name")
    private String preferredUserName;

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

    public String getInbox() {
        return inbox;
    }

    public void setInbox(String inbox) {
        this.inbox = inbox;
    }

    public String getOutbox() {
        return outbox;
    }

    public void setOutbox(String outbox) {
        this.outbox = outbox;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getPreferredUserName() {
        return preferredUserName;
    }

    public void setPreferredUserName(String preferredUserName) {
        this.preferredUserName = preferredUserName;
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
}
