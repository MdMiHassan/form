package mdmihassan.form.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import mdmihassan.form.auth.GrantedAuthorityImpl;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserTokenDto {

    private UUID id;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[\\p{L}\\d\\s\\-._']+$",
            message = "only letters, digits, spaces, hyphens, apostrophes, and periods are allowed")
    @Size(min = 1, max = 30, message = "must be between 1 and 30 characters")
    private String name;

    private String secret;

    private Boolean enabled;

    private Instant issuedAt;

    private Instant expiration;

    @JsonDeserialize(contentAs = GrantedAuthorityImpl.class)
    private List<? extends GrantedAuthority> authorities;

}
