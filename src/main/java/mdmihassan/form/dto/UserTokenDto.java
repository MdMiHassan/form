package mdmihassan.form.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTokenDto {

    private String name;
    private String secret;
    private boolean enabled;
    private Instant issuedAt;
    private Instant expiration;
    private List<GrantedAuthority> authorities;

}
