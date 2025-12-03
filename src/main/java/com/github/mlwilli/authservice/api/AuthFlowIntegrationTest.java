package com.github.mlwilli.authservice.api;

import com.github.mlwilli.authservice.api.dto.LoginRequest;
import com.github.mlwilli.authservice.api.dto.LoginResponse;
import com.github.mlwilli.authservice.api.dto.UserResponse;
import org.testng.annotations.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthFlowIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void login_and_access_protected_endpoint() {
        // Login as demo admin user
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("admin123");

        ResponseEntity<LoginResponse> loginResponse =
                restTemplate.postForEntity(url("/api/auth/login"), request, LoginResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponse body = loginResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getToken()).isNotBlank();
        assertThat(body.getUsername()).isEqualTo("admin");

        // Use JWT to call a protected endpoint
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(body.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<UserResponse[]> usersResponse = restTemplate.exchange(
                url("/api/users"),
                HttpMethod.GET,
                entity,
                UserResponse[].class
        );

        assertThat(usersResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        UserResponse[] users = usersResponse.getBody();
        assertThat(users).isNotNull();
        assertThat(users.length).isGreaterThanOrEqualTo(1);
    }

    @Test
    void accessing_protected_endpoint_without_token_should_fail() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(url("/api/users"), String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }
}
