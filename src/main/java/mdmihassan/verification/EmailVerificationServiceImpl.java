package mdmihassan.verification;

import org.springframework.stereotype.Service;

@Service
class EmailVerificationServiceImpl implements EmailVerificationService {

    @Override
    public boolean verifySecret(String email, String authCode) {
        return true; // for now keeping it true for testing
    }

    @Override
    public boolean verifySecret(String url) {
        return false;
    }

    @Override
    public void issueChallenge(String email) {
    }

}
