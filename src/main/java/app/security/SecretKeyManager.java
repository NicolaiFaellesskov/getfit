
package app.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SecretKeyManager {

    private static final Path KEY_FILE =
            Path.of("config", "jwt-secret.key");

    public static SecretKey getSecretKey() {

        String secret = System.getenv("JWT_SECRET_KEY");

        if (secret != null && !secret.isBlank()) {
            return Keys.hmacShaKeyFor(
                    Decoders.BASE64.decode(secret)
            );
        }

        try {
            if (Files.exists(KEY_FILE)) {
                secret = Files.readString(KEY_FILE).trim();

                return Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(secret)
                );
            }

            Files.createDirectories(KEY_FILE.getParent());

            SecretKey key = Keys.secretKeyFor(
                    io.jsonwebtoken.SignatureAlgorithm.HS256
            );

            secret = Encoders.BASE64.encode(key.getEncoded());
            Files.writeString(KEY_FILE, secret);

            return key;

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load or create JWT secret key", e
            );
        }
    }
}
