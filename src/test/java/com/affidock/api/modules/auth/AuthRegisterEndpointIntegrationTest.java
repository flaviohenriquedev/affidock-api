package com.affidock.api.modules.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthRegisterEndpointIntegrationTest {

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturnValidationErrorWhenEmailIsInvalid() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/v1/auth/register"))
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
            """
            {
              "name": "Usuario Teste",
              "email": "email-invalido",
              "password": "senha12345"
            }
            """
                )
            )
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<?, ?> body = objectMapper.readValue(response.body(), Map.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        assertNotNull(body);
        assertEquals("common.validation.invalid", body.get("code"));
    }

    @Test
    void shouldReturnValidationErrorWhenPasswordIsTooShort() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:" + port + "/api/v1/auth/register"))
            .header("Content-Type", "application/json")
            .POST(
                HttpRequest.BodyPublishers.ofString(
            """
            {
              "name": "Usuario Teste",
              "email": "user-short-pass@affidock.com",
              "password": "123"
            }
            """
                )
            )
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Map<?, ?> body = objectMapper.readValue(response.body(), Map.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode());
        assertNotNull(body);
        assertEquals("common.validation.invalid", body.get("code"));

        Object detailsObj = body.get("details");
        assertTrue(detailsObj instanceof List<?>);
        List<?> details = (List<?>) detailsObj;
        assertTrue(details.contains("auth.validation.password.min-length"));
    }
}
