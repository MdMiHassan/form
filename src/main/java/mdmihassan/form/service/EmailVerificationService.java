package mdmihassan.form.service;

public interface EmailVerificationService {

    boolean verifyByAuthCode(String email, String authCode);

    boolean verifyByURL(String url);

    void sendChallenge(String email);

}
