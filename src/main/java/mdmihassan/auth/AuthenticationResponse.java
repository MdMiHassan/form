package mdmihassan.auth;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class AuthenticationResponse {

    private String refreshToken;
    private String accessToken;

    public AuthenticationResponse(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public static AuthenticationResponse of(String refreshToken, String accessToken) {
        return new AuthenticationResponse(refreshToken, accessToken);
    }

}
