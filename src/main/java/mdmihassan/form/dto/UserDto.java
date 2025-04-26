package mdmihassan.form.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mdmihassan.form.entity.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;

    @NotNull(message = "fistName must required")
    @NotBlank(message = "fistName must not be empty")
    private String firstName;

    @NotNull(message = "fistName must required")
    @NotBlank(message = "fistName must not be empty")
    private String lastName;

    @NotNull(message = "fistName must required")
    @NotBlank(message = "fistName must not be empty")
    private String username;

    @NotNull(message = "fistName must required")
    @NotBlank(message = "fistName must not be empty")
    private String password;

    @NotNull(message = "fistName must required")
    @NotBlank(message = "fistName must not be empty")
    @Email(message = "email must be represent a valid email address")
    private String email;

    private List<User.Role> roles;

    private boolean locked;

    private boolean enabled;

    private Timestamp accountExpiration;

    private Timestamp credentialsExpiration;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
