package mdmihassan.form.service;

public interface TokenService<T> {

    T parse(String token);

    String generate(T token);

}