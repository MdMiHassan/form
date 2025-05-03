package mdmihassan.form.model;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mdmihassan.form.dto.UserDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestModel {

    private String authCode;

    @Valid
    private UserDto user;

}
