package mdmihassan.form.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotNull(message = "Username shouldn't be empty")
    @NotBlank(message = "Username shouldn't be blank")
    private String username;

    @NotNull(message = "Password shouldn't be empty")
    @NotBlank(message = "Password shouldn't be blank")
    private String password;

    @NotNull(message = "Login Token shouldn't be empty")
    private UUID loginToken;
}
