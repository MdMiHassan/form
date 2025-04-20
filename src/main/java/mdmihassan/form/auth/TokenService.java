package mdmihassan.form.auth;

public interface TokenService {

    UserAuthorityToken verify(String token);

    String generate(UserAuthorityToken userAuthorityToken);

}