package mdmihassan.form.auth;

import org.springframework.security.authentication.AccountStatusException;

public class PrimaryEmailNotVerifiedException extends AccountStatusException {

    public PrimaryEmailNotVerifiedException(String message) {
        super(message);
    }

    public PrimaryEmailNotVerifiedException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
