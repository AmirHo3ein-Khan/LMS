package ir.lms.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import ir.lms.util.KeyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    @Value("${app.security.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public JwtService() throws Exception {
        this.privateKey = KeyUtils.loadPrivateKey("keys/local_only/private_key.pem");
        this.publicKey = KeyUtils.loadPublicKey("keys/local_only/public_key.pem");
    }

    public String generateAccessToken(final UUID uuid) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "ACCESS_TOKEN");
        return buildToken(uuid, claims, this.accessTokenExpiration);
    }

    public String generateRefreshToken(final UUID uuid) {
        final Map<String, Object> claims = Map.of(TOKEN_TYPE, "REFRESH_TOKEN");
        return buildToken(uuid, claims, this.refreshTokenExpiration);
    }

    public String buildToken(final UUID uuid, final Map<String, Object> claims, final long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(uuid.toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)
                .compact();
    }

    public boolean isTokenValid(final String token, final String expectedUUID) {
        final String uuid = extractUUID(token);
        return uuid.equals(expectedUUID) && !isTokenExpired(token);
    }

    public String extractUUID(final String token) {
        return extractClaims(token).getSubject();
    }

    private boolean isTokenExpired(final String token) {
        return extractClaims(token).getExpiration()
                .before(new Date());
    }

    public Claims extractClaims(final String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (final JwtException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

//    public String refreshAccessToken(final String refreshToken) {
//        final Claims claims = extractClaims(refreshToken);
//
//        if (!"REFRESH_TOKEN".equals(claims.get(TOKEN_TYPE, String.class))) {
//            throw new RuntimeException("Invalid token type");
//        }
//        if (claims.getExpiration().before(new Date())) {
//            throw new RuntimeException("Refresh token expired");
//        }
//
//        final String username = claims.getSubject();
//        return generateAccessToken(username);
//    }
}