package mdmihassan.auth.token;

public interface TokenService<T> {

    T parse(String token);

    String generate(T token);

}