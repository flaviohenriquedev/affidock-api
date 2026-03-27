package com.affidock.api.common.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final JwsHeader jwsHeader;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        SecretKey secretKey = new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build();
        this.jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
            .subject(subject)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtProperties.getAccessExpirationSeconds()))
            .claims(map -> map.putAll(claims))
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }

    public String generateRefreshToken(String subject, String tokenId) {
        Instant now = Instant.now();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
            .subject(subject)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(jwtProperties.getRefreshExpirationSeconds()))
            .claim("tokenType", "refresh")
            .claim("jti", tokenId)
            .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet)).getTokenValue();
    }

    public Jwt decode(String token) {
        return jwtDecoder.decode(token);
    }

    public long getAccessExpirationSeconds() {
        return jwtProperties.getAccessExpirationSeconds();
    }

    public Map<String, Object> defaultAccessClaims(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("email", email);
        claims.put("scope", "api");
        return claims;
    }
}
