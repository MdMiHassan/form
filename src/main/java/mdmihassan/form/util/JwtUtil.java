package mdmihassan.form.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    public static boolean isExpired(Claims claims) {
        return Date.from(Instant.now()).after(claims.getExpiration());
    }

    public static boolean nonExpired(Claims claims) {
        return Date.from(Instant.now()).before(claims.getExpiration());
    }

    public static Jws<Claims> parseClaims(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public static String generateJwt(SecretKey secreteKey,
                                     String subject,
                                     Instant issuedAt,
                                     Instant expiration,
                                     Map<String, Object> claims) {
        return buildJsonWebToken(secreteKey, subject, issuedAt, expiration, claims);
    }

    public static String generateJwt(SecretKey secreteKey, String subject,
                                     Instant issuedAt,
                                     Instant expiration) {
        return buildJsonWebToken(secreteKey, subject, issuedAt, expiration, Collections.emptyMap());
    }

    private static String buildJsonWebToken(SecretKey secretKey,
                                            String subject,
                                            Instant issuedAt,
                                            Instant expiration,
                                            Map<String, Object> claims) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .and()
                .subject(subject)
                .issuedAt(TimeAndDates.toDate(issuedAt))
                .expiration(TimeAndDates.toDate(expiration))
                .signWith(secretKey)
                .compact();
    }

}
