package mdmihassan.form.service.impl;

import mdmihassan.form.service.EmailVerificationService;
import org.springframework.stereotype.Service;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Override
    public boolean verifyByAuthCode(String email, String authCode) {
        return true; // for now keeping it true for testing
    }

    @Override
    public boolean verifyByURL(String url) {
        return false;
    }

    @Override
    public void sendChallenge(String email) {
    }

}
