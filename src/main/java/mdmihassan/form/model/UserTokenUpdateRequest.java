package mdmihassan.form.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mdmihassan.form.auth.GrantedAuthorityImpl;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTokenUpdateRequest {

    @Pattern(regexp = "^[\\p{L}\\d\\s\\-._']+$",
            message = "Only letters, digits, spaces, hyphens, apostrophes, and periods are allowed")
    @Size(min = 1, max = 30, message = "name must be between 1 and 30 characters")
    private String name;

    private Boolean enabled;

    private Instant expiration;

    @JsonDeserialize(contentAs = GrantedAuthorityImpl.class)
    private List<? extends GrantedAuthority> authorities;

}
