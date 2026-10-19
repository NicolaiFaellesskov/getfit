
package app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

public class TokenService {

    private static final String ISSUER = "cphbusiness.dat";
    private static final long EXPIRATION_TIME = 3_600_000;

    private final SecretKey secretKey;

    public TokenService() {
        this.secretKey = SecretKeyManager.getSecretKey();
    }

    public String generateToken(Integer userId) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .issuer(ISSUER)
                .subject(userId.toString())
                .issuedAt(new Date(now))
                .expiration(new Date(now + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact();
    }


    public Integer validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Integer.parseInt(claims.getSubject());

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired JWT", e);
        }
    }
}