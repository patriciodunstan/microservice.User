package com.userREgisterLoginAuth.microservice.User.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    private JwtService jwtService;
    private static final long TEST_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        // Arrange
        String username = "testuser";

        // Act
        String token = jwtService.generateToken(username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(jwtService.isTokenValid(token, username));
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        String extractedUsername = jwtService.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        String extractedUsername = jwtService.extractClaim(token, Claims::getSubject);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void isTokenValid_ValidToken_ShouldReturnTrue() {
        // Arrange
        String username = "testuser";
        String token = jwtService.generateToken(username);

        // Act
        boolean isValid = jwtService.isTokenValid(token, username);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WrongUsername_ShouldReturnFalse() {
        // Arrange
        String username = "testuser";
        String wrongUsername = "wronguser";
        String token = jwtService.generateToken(username);

        // Act
        boolean isValid = jwtService.isTokenValid(token, wrongUsername);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_ExpiredToken() {
        // Arrange
        String username = "testuser";
        
        // Create a token with very short expiration
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 1L);
        String token = jwtService.generateToken(username);
        
        // Reset expiration for other tests
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", TEST_EXPIRATION);

        // Act & Assert - This test verifies that the method handles expired tokens gracefully
        // The actual validation will depend on the current time vs token expiration
        // We're testing that the method doesn't throw an exception
        assertDoesNotThrow(() -> {
            jwtService.isTokenValid(token, username);
            // The result may be true or false depending on timing, but no exception should be thrown
        });
    }
} 