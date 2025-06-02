package mdmihassan.auth.token;

import jakarta.persistence.*;
import lombok.*;
import mdmihassan.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tokens")
public class UserToken {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne(cascade = CascadeType.DETACH)
    private User user;

    @Column(nullable = false)
    private String name;

    private boolean enabled;

    @Column(updatable = false, unique = true)
    private String secret;

    private Timestamp lastConnectedAt;

    private Timestamp credentialsExpiration;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<TokenAuthorities> tokenAuthorities;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public List<GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (TokenAuthorities auth : tokenAuthorities) {
            authorities.add(new SimpleGrantedAuthority(auth.toString()));
        }
        return authorities;
    }

    public void setTokenAuthorities(List<? extends GrantedAuthority> grantedAuthorities) {
        Set<TokenAuthorities> authorities = new HashSet<>();
        for (GrantedAuthority auth : grantedAuthorities) {
            authorities.add(TokenAuthorities.from(auth.getAuthority()));
        }
        tokenAuthorities = authorities;
    }

    public void addTokenAuthorities(List<? extends GrantedAuthority> grantedAuthorities) {
        if (tokenAuthorities == null) {
            tokenAuthorities = new HashSet<>();
        }
        for (GrantedAuthority auth : grantedAuthorities) {
            tokenAuthorities.add(TokenAuthorities.from(auth.getAuthority()));
        }
    }

    public boolean isTokenAuthenticated() {
        return enabled && (credentialsExpiration == null || credentialsExpiration.before(Timestamp.from(Instant.now())));
    }

    public UserToken disable() {
        enabled = false;
        return this;
    }

    public enum TokenAuthorities {

        FORM_CREATE,
        FORM_UPDATE,
        FORM_DELETE,
        FORM_VIEW;

        public static TokenAuthorities from(String tokenAuthorities) {
            if (tokenAuthorities == null) {
                throw new IllegalArgumentException("token authorities must not be null");
            }
            return switch (tokenAuthorities.toLowerCase()) {
                case "form_create" -> FORM_CREATE;
                case "form_update" -> FORM_UPDATE;
                case "form_delete" -> FORM_DELETE;
                case "form_view" -> FORM_VIEW;
                default -> throw new IllegalArgumentException("Unexpected value: " + tokenAuthorities.toLowerCase());
            };
        }
    }

}
