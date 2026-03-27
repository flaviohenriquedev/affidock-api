package com.affidock.api.modules.auth.service;

import com.affidock.api.common.domain.EntityStatus;
import com.affidock.api.common.exception.WarningException;
import com.affidock.api.common.security.JwtService;
import com.affidock.api.modules.auth.domain.RefreshTokenEntity;
import com.affidock.api.modules.auth.dto.GoogleExchangeRequest;
import com.affidock.api.modules.auth.dto.LoginRequest;
import com.affidock.api.modules.auth.dto.RegisterRequest;
import com.affidock.api.modules.auth.dto.SetupPasswordRequest;
import com.affidock.api.modules.auth.dto.TokenResponse;
import com.affidock.api.modules.auth.repository.RefreshTokenRepository;
import com.affidock.api.modules.users.domain.AuthProvider;
import com.affidock.api.modules.users.domain.UserEntity;
import com.affidock.api.modules.users.repository.UserRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
        UserRepository userRepository,
        RefreshTokenRepository refreshTokenRepository,
        JwtService jwtService,
        PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TokenResponse exchangeGoogle(GoogleExchangeRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        UserEntity user = userRepository.findByEmail(normalizedEmail)
            .map(existing -> updateGoogleUser(existing, request))
            .orElseGet(() -> createGoogleUser(request, normalizedEmail));

        userRepository.save(user);
        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        UserEntity user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new WarningException("auth.user.not.registered"));

        if (user.getProvider() == AuthProvider.GOOGLE && user.getPasswordHash() == null) {
            throw new WarningException("auth.account.google.only");
        }

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new WarningException("auth.credentials.invalid");
        }

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new WarningException("auth.signup.email.already-used");
        }

        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setStatus(EntityStatus.ATIVO);
        user.setName(request.name().trim());
        user.setEmail(normalizedEmail);
        user.setProvider(AuthProvider.LOCAL);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return issueTokens(user);
    }

    @Transactional
    public void setupPassword(SetupPasswordRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        UserEntity user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new WarningException("auth.user.not.registered"));

        if (user.getProvider() != AuthProvider.GOOGLE) {
            throw new WarningException("auth.password.setup.not-allowed");
        }

        user.setPasswordHash(passwordEncoder.encode(request.password()));
        userRepository.save(user);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        Jwt decoded = jwtService.decode(refreshToken);
        String tokenType = decoded.getClaimAsString("tokenType");
        if (!"refresh".equals(tokenType)) {
            throw new WarningException("auth.refresh.invalid");
        }
        String tokenId = decoded.getClaimAsString("jti");
        RefreshTokenEntity tokenEntity = refreshTokenRepository.findByTokenId(tokenId)
            .orElseThrow(() -> new WarningException("auth.refresh.invalid"));

        if (tokenEntity.getRevokedAt() != null || tokenEntity.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new WarningException("auth.refresh.expired");
        }

        tokenEntity.setRevokedAt(OffsetDateTime.now());
        refreshTokenRepository.save(tokenEntity);

        return issueTokens(tokenEntity.getUser());
    }

    @Transactional
    public void revoke(String refreshToken) {
        Jwt decoded = jwtService.decode(refreshToken);
        String tokenId = decoded.getClaimAsString("jti");
        refreshTokenRepository.findByTokenId(tokenId).ifPresent(entity -> {
            entity.setRevokedAt(OffsetDateTime.now());
            refreshTokenRepository.save(entity);
        });
    }

    private TokenResponse issueTokens(UserEntity user) {
        String accessToken = jwtService.generateAccessToken(
            user.getEmail(),
            jwtService.defaultAccessClaims(user.getId().toString(), user.getEmail())
        );

        String tokenId = UUID.randomUUID().toString();
        String refreshToken = jwtService.generateRefreshToken(user.getEmail(), tokenId);

        RefreshTokenEntity refreshEntity = new RefreshTokenEntity();
        refreshEntity.setId(UUID.randomUUID());
        refreshEntity.setStatus(EntityStatus.ATIVO);
        refreshEntity.setUser(user);
        refreshEntity.setTokenId(tokenId);
        refreshEntity.setExpiresAt(OffsetDateTime.ofInstant(Instant.now().plusSeconds(2_592_000), ZoneOffset.UTC));
        refreshTokenRepository.save(refreshEntity);

        return new TokenResponse(accessToken, refreshToken, "Bearer", jwtService.getAccessExpirationSeconds());
    }

    private UserEntity createGoogleUser(GoogleExchangeRequest request, String normalizedEmail) {
        UserEntity user = new UserEntity();
        user.setId(UUID.randomUUID());
        user.setStatus(EntityStatus.ATIVO);
        user.setEmail(normalizedEmail);
        user.setName(request.name());
        user.setAvatarUrl(request.picture());
        user.setGoogleSubject(request.googleSubject());
        user.setProvider(AuthProvider.GOOGLE);
        return user;
    }

    private UserEntity updateGoogleUser(UserEntity user, GoogleExchangeRequest request) {
        user.setName(request.name());
        user.setAvatarUrl(request.picture());
        user.setGoogleSubject(request.googleSubject());
        user.setProvider(AuthProvider.GOOGLE);
        return user;
    }
}
