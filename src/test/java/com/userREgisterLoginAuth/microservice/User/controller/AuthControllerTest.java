package com.userREgisterLoginAuth.microservice.User.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userREgisterLoginAuth.microservice.User.dto.AuthResponseDto;
import com.userREgisterLoginAuth.microservice.User.dto.LoginDto;
import com.userREgisterLoginAuth.microservice.User.dto.UserInfoDto;
import com.userREgisterLoginAuth.microservice.User.dto.UserRegistrationDto;
import com.userREgisterLoginAuth.microservice.User.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registerUser_Success() throws Exception {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password123");
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");

        UserInfoDto userInfo = new UserInfoDto("1", "test@example.com", "testuser", "John", "Doe", "USER");
        AuthResponseDto response = new AuthResponseDto("jwtToken", "User registered successfully", true);
        response.setUser(userInfo);

        when(authService.registerUser(any(UserRegistrationDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.token").value("jwtToken"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void registerUser_ValidationError() throws Exception {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_EmailAlreadyExists() throws Exception {
        // Arrange
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("test@example.com");
        registrationDto.setUsername("testuser");
        registrationDto.setPassword("password123");
        registrationDto.setFirstName("John");
        registrationDto.setLastName("Doe");

        AuthResponseDto response = new AuthResponseDto(null, "Email already registered", false);
        when(authService.registerUser(any(UserRegistrationDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void loginUser_Success() throws Exception {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");

        UserInfoDto userInfo = new UserInfoDto("1", "test@example.com", "testuser", "John", "Doe", "USER");
        AuthResponseDto response = new AuthResponseDto("jwtToken", "Login successful", true);
        response.setUser(userInfo);

        when(authService.loginUser(any(LoginDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.token").value("jwtToken"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    void loginUser_InvalidCredentials() throws Exception {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("wrongpassword");

        AuthResponseDto response = new AuthResponseDto(null, "Invalid email or password", false);
        when(authService.loginUser(any(LoginDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    void loginUser_ValidationError() throws Exception {
        // Arrange
        LoginDto loginDto = new LoginDto();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void healthCheck() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auth Service is running!"));
    }
} 