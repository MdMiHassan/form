package mdmihassan.form.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;

    private boolean locked;

    private boolean enabled;

    private Timestamp accountExpiration;

    private Timestamp credentialsExpiration;

    private boolean emailVerified;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Override
    public List<GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.toString()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        if (accountExpiration == null) {
            return true;
        }
        return accountExpiration.before(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (credentialsExpiration == null) {
            return true;
        }
        return credentialsExpiration.before(new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasRole(Role role) {
        return roles != null && roles.contains(role);
    }

    public void addRoles(List<Role> roles) {
        if (roles == null) {
            return;
        }
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        for (Role role : roles) {
            if (role != null) {
                this.roles.add(role);
            }
        }
    }

    public enum Role {
        ADMIN,
        USER
    }

}
