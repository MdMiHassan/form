package mdmihassan.form.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class ApiKey {
    private String name;
    private String secret;
    private boolean enabled;
    private Instant issuedAt;
    private Instant expiration;
    private List<GrantedAuthority> authorities;
}
