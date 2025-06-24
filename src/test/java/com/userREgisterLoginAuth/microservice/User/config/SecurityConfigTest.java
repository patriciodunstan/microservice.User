package com.userREgisterLoginAuth.microservice.User.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void passwordEncoder_ShouldBeConfigured() {
        // Arrange
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "testPassword123";

        // Act
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // Assert
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(passwordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void passwordEncoder_ShouldGenerateDifferentEncodings() {
        // Arrange
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = "samePassword123";

        // Act
        String encoding1 = passwordEncoder.encode(password);
        String encoding2 = passwordEncoder.encode(password);

        // Assert
        assertNotEquals(encoding1, encoding2);
        assertTrue(passwordEncoder.matches(password, encoding1));
        assertTrue(passwordEncoder.matches(password, encoding2));
    }

    @Test
    void passwordEncoder_ShouldHandleSpecialCharacters() {
        // Arrange
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordWithSpecialChars = "P@ssw0rd!@#$%^&*()";

        // Act
        String encoded = passwordEncoder.encode(passwordWithSpecialChars);

        // Assert
        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches(passwordWithSpecialChars, encoded));
    }

    @Test
    void passwordEncoder_ShouldHandleEmptyPassword() {
        // Arrange
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String emptyPassword = "";

        // Act
        String encoded = passwordEncoder.encode(emptyPassword);

        // Assert
        assertNotNull(encoded);
        assertTrue(passwordEncoder.matches(emptyPassword, encoded));
    }
} 