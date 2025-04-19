package mdmihassan.form.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.sql.Timestamp;
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

    private enum TokenAuthorities {
        FORM_CREATE,
        FORM_UPDATE,
        FORM_DELETE,
    }

}
