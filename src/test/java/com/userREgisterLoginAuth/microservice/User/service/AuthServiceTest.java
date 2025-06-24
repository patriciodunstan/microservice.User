package com.userREgisterLoginAuth.microservice.User.service;

import com.userREgisterLoginAuth.microservice.User.dto.AuthResponseDto;
import com.userREgisterLoginAuth.microservice.User.dto.LoginDto;
import com.userREgisterLoginAuth.microservice.User.dto.UserInfoDto;
import com.userREgisterLoginAuth.microservice.User.dto.UserRegistrationDto;
import com.userREgisterLoginAuth.microservice.User.model.User;
import com.userREgisterLoginAuth.microservice.User.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private UserRegistrationDto validRegistrationDto;
    private LoginDto validLoginDto;
    private User mockUser;

    @BeforeEach
    void setUp() {
        validRegistrationDto = new UserRegistrationDto();
        validRegistrationDto.setEmail("test@example.com");
        validRegistrationDto.setUsername("testuser");
        validRegistrationDto.setPassword("password123");
        validRegistrationDto.setFirstName("John");
        validRegistrationDto.setLastName("Doe");

        validLoginDto = new LoginDto();
        validLoginDto.setEmail("test@example.com");
        validLoginDto.setPassword("password123");

        mockUser = new User();
        mockUser.setId("1");
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setPassword("encodedPassword");
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setRole("USER");
        mockUser.setEnabled(true);
        mockUser.setCreatedAt(LocalDateTime.now());
        mockUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void registerUser_Success() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(anyString())).thenReturn("jwtToken");

        // Act
        AuthResponseDto result = authService.registerUser(validRegistrationDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("User registered successfully", result.getMessage());
        assertEquals("jwtToken", result.getToken());
        assertNotNull(result.getUser());
        assertEquals("test@example.com", result.getUser().getEmail());
        assertEquals("testuser", result.getUser().getUsername());

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).existsByUsername("testuser");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("testuser");
    }

    @Test
    void registerUser_EmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act
        AuthResponseDto result = authService.registerUser(validRegistrationDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Email already registered", result.getMessage());
        assertNull(result.getToken());

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_UsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act
        AuthResponseDto result = authService.registerUser(validRegistrationDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Username already taken", result.getMessage());
        assertNull(result.getToken());

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithSpecialCharacters() {
        // Arrange
        UserRegistrationDto specialDto = new UserRegistrationDto();
        specialDto.setEmail("test+tag@example.com");
        specialDto.setUsername("test_user_123");
        specialDto.setPassword("password@123!");
        specialDto.setFirstName("José");
        specialDto.setLastName("García-López");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(anyString())).thenReturn("jwtToken");

        // Act
        AuthResponseDto result = authService.registerUser(specialDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("User registered successfully", result.getMessage());
    }

    @Test
    void loginUser_Success() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("jwtToken");

        // Act
        AuthResponseDto result = authService.loginUser(validLoginDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Login successful", result.getMessage());
        assertEquals("jwtToken", result.getToken());
        assertNotNull(result.getUser());
        assertEquals("test@example.com", result.getUser().getEmail());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService).generateToken("testuser");
    }

    @Test
    void loginUser_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act
        AuthResponseDto result = authService.loginUser(validLoginDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Invalid email or password", result.getMessage());
        assertNull(result.getToken());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginUser_InvalidPassword() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act
        AuthResponseDto result = authService.loginUser(validLoginDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Invalid email or password", result.getMessage());
        assertNull(result.getToken());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void loginUser_AccountDisabled() {
        // Arrange
        mockUser.setEnabled(false);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Act
        AuthResponseDto result = authService.loginUser(validLoginDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Account is disabled", result.getMessage());
        assertNull(result.getToken());

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void loginUser_WithDifferentEmailCase() {
        // Arrange
        LoginDto upperCaseDto = new LoginDto();
        upperCaseDto.setEmail("TEST@EXAMPLE.COM");
        upperCaseDto.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("jwtToken");

        // Act
        AuthResponseDto result = authService.loginUser(upperCaseDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("Login successful", result.getMessage());
    }

    @Test
    void registerUser_RepositoryException() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.registerUser(validRegistrationDto);
        });
    }

    @Test
    void loginUser_RepositoryException() {
        // Arrange
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authService.loginUser(validLoginDto);
        });
    }

    @Test
    void registerUser_EmptyFields() {
        // Arrange
        UserRegistrationDto emptyDto = new UserRegistrationDto();
        emptyDto.setEmail("");
        emptyDto.setUsername("");
        emptyDto.setPassword("");
        emptyDto.setFirstName("");
        emptyDto.setLastName("");

        // Mock repository calls for empty fields
        when(userRepository.existsByEmail("")).thenReturn(false);
        when(userRepository.existsByUsername("")).thenReturn(false);
        when(passwordEncoder.encode("")).thenReturn("encodedEmptyPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtService.generateToken(anyString())).thenReturn("jwtToken");

        // Act
        AuthResponseDto result = authService.registerUser(emptyDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("User registered successfully", result.getMessage());
    }

    @Test
    void loginUser_EmptyFields() {
        // Arrange
        LoginDto emptyDto = new LoginDto();
        emptyDto.setEmail("");
        emptyDto.setPassword("");

        // Act
        AuthResponseDto result = authService.loginUser(emptyDto);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("Invalid email or password", result.getMessage());
    }
} 