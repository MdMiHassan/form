package mdmihassan.verification;

public interface VarificationService<IDENTITY, SECRET> {

    boolean verifySecret(IDENTITY identity, SECRET secret);

    boolean verifySecret(SECRET secret);

    void issueChallenge(IDENTITY identity);

}
