package mdmihassan.form.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mdmihassan.form.auth.GrantedAuthorityImpl;
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

    @NotNull(message = "firstName is required")
    @NotBlank(message = "firstName must not be empty")
    @Pattern(regexp = "^[\\p{L}\\s\\-.']+$",
            message = "firstName can only contain letters, spaces, hyphens, periods, and apostrophes")
    @Size(min = 1, max = 50, message = "firstName must be between 1 and 50 characters")
    private String firstName;

    @NotNull(message = "lastName is required")
    @NotBlank(message = "lastName must not be empty")
    @Pattern(regexp = "^[\\p{L}\\s\\-.']+$",
            message = "lastName can only contain letters, spaces, hyphens, periods, and apostrophes")
    @Size(min = 1, max = 50, message = "lastName must be between 1 and 50 characters")
    private String lastName;

    @NotNull(message = "username is required")
    @NotBlank(message = "username must not be empty")
    @Pattern(regexp = "^\\w+$", message = "username can only contain letters, digits, underscores, and periods")
    @Size(min = 8, max = 30, message = "username must be between 8 and 30 characters")
    private String username;

    @NotNull(message = "password is required")
    @NotBlank(message = "password must not be empty")
    @Size(min = 8, max = 64, message = "password must be between 8 and 64 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+=~])[A-Za-z\\d@$!%*?&#^()_+=~]{8,64}$",
            message = "password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @NotNull(message = "primaryEmail is required")
    @NotBlank(message = "primaryEmail must not be empty")
    @Email(message = "primaryEmail must represent a valid email address")
    @Size(max = 100, message = "primaryEmail must not exceed 100 characters")
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
