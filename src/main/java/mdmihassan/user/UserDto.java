package mdmihassan.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mdmihassan.auth.GrantedAuthorityImpl;
import org.springframework.security.core.GrantedAuthority;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private UUID id;

    @NotBlank
    @NotNull
    @Pattern(regexp = "^[\\p{L}\\s\\-.']+$",
            message = "can only contain letters, spaces, hyphens, periods, and apostrophes")
    @Size(min = 1, max = 50, message = "must be between 1 and 50 characters")
    private String firstName;

    @NotBlank
    @NotNull
    @Pattern(regexp = "^[\\p{L}\\s\\-.']+$",
            message = "can only contain letters, spaces, hyphens, periods, and apostrophes")
    @Size(min = 1, max = 50, message = "must be between 1 and 50 characters")
    private String lastName;

    @NotBlank
    @NotNull
    @Pattern(regexp = "^\\w+$", message = "can only contain letters, digits, underscores, and periods")
    @Size(min = 8, max = 30, message = "must be between 8 and 30 characters")
    private String username;

    @NotBlank
    @NotNull
    @Size(min = 8, max = 64, message = "must be between 8 and 64 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+=~])[A-Za-z\\d@$!%*?&#^()_+=~]{8,64}$",
            message = "must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @Email
    @NotBlank
    @NotNull
    @Size(max = 100, message = "must not exceed 100 characters")
    private String primaryEmail;

    @JsonDeserialize(contentAs = GrantedAuthorityImpl.class)
    private List<? extends GrantedAuthority> authorities;

    private boolean locked;

    private boolean enabled;

    private Timestamp accountExpiration;

    private Timestamp credentialsExpiration;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}
