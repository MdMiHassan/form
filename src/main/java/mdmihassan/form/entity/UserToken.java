package mdmihassan.form.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @ManyToOne
    private User user;

    private String name;

    private boolean enabled;

    @Column(updatable = false, unique = true)
    private String tokenHash;

    private Timestamp lastConnectedAt;

    private Timestamp credentialsExpiration;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<TokenAuthorities> tokenAuthorities;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    public List<GrantedAuthority> getTokenAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (TokenAuthorities auth : tokenAuthorities) {
            authorities.add(new SimpleGrantedAuthority(auth.toString()));
        }
        return authorities;
    }

    public void setTokenAuthorities(List<GrantedAuthority> grantedAuthorities) {
        List<TokenAuthorities> authorities = new ArrayList<>();
        for (GrantedAuthority auth : grantedAuthorities) {
            authorities.add(TokenAuthorities.of(auth.getAuthority()));
        }
        tokenAuthorities = authorities;
    }

    public void addTokenAuthorities(List<GrantedAuthority> grantedAuthorities) {
        if (tokenAuthorities == null) {
            tokenAuthorities = new ArrayList<>();
        }
        for (GrantedAuthority auth : grantedAuthorities) {
            tokenAuthorities.add(TokenAuthorities.of(auth.getAuthority()));
        }
    }

    public boolean isValidToken() {
        return enabled && (credentialsExpiration == null || Timestamp.from(Instant.now()).before(credentialsExpiration));
    }

    public void disable() {
        enabled = false;
    }

    private enum TokenAuthorities {

        FORM_CREATE,
        FORM_UPDATE,
        FORM_DELETE;

        public static TokenAuthorities of(String tokenAuthorities) {
            if (tokenAuthorities == null) {
                return null;
            }
            return switch (tokenAuthorities.toLowerCase()) {
                case "form_create" -> FORM_CREATE;
                case "form_update" -> FORM_UPDATE;
                case "form_delete" -> FORM_DELETE;
                default -> throw new IllegalStateException("Unexpected value: " + tokenAuthorities.toLowerCase());
            };
        }
    }

}
