package com.affidock.api.modules.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.affidock.api.common.exception.WarningException;
import com.affidock.api.modules.auth.dto.RegisterRequest;
import com.affidock.api.modules.auth.dto.TokenResponse;
import com.affidock.api.modules.auth.service.AuthService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AuthRegisterIntegrationTest {

    @Autowired
    private AuthService authService;

    @Test
    void shouldRegisterAndReturnTokens() {
        String email = "register-" + UUID.randomUUID() + "@affidock.com";

        TokenResponse response = authService.register(new RegisterRequest("Usuario Teste", email, "senha12345"));

        assertNotNull(response.accessToken());
        assertNotNull(response.refreshToken());
        assertEquals("Bearer", response.tokenType());
        assertNotNull(response.expiresIn());
    }

    @Test
    void shouldReturnWarningWhenEmailAlreadyExists() {
        String email = "used-" + UUID.randomUUID() + "@affidock.com";
        authService.register(new RegisterRequest("Usuario Existente", email, "senha12345"));

        WarningException exception = assertThrows(
            WarningException.class,
            () -> authService.register(new RegisterRequest("Outro Usuario", email, "senha12345"))
        );

        assertEquals("auth.signup.email.already-used", exception.getCode());
    }
}
