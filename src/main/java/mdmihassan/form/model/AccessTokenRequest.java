package mdmihassan.form.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessTokenRequest {

    @NotNull(message = "Refresh token shouldn't be empty")
    @NotBlank(message = "Refresh token shouldn't be blank")
    private String refreshToken;
}
