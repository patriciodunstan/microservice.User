package com.userREgisterLoginAuth.microservice.User.service;

import com.userREgisterLoginAuth.microservice.User.dto.AuthResponseDto;
import com.userREgisterLoginAuth.microservice.User.dto.LoginDto;
import com.userREgisterLoginAuth.microservice.User.dto.UserInfoDto;
import com.userREgisterLoginAuth.microservice.User.dto.UserRegistrationDto;
import com.userREgisterLoginAuth.microservice.User.model.User;
import com.userREgisterLoginAuth.microservice.User.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;
    
    public AuthResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            return new AuthResponseDto(null, "Email already registered", false);
        }
        
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            return new AuthResponseDto(null, "Username already taken", false);
        }
        
        // Create new user
        User user = new User(
            registrationDto.getEmail(),
            registrationDto.getUsername(),
            passwordEncoder.encode(registrationDto.getPassword()),
            registrationDto.getFirstName(),
            registrationDto.getLastName()
        );
        
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String token = jwtService.generateToken(savedUser.getUsername());
        
        // Create user info DTO
        UserInfoDto userInfo = new UserInfoDto(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getUsername(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getRole()
        );
        
        AuthResponseDto response = new AuthResponseDto(token, "User registered successfully", true);
        response.setUser(userInfo);
        
        return response;
    }
    
    public AuthResponseDto loginUser(LoginDto loginDto) {
        Optional<User> userOptional = userRepository.findByEmail(loginDto.getEmail());
        
        if (userOptional.isEmpty()) {
            return new AuthResponseDto(null, "Invalid email or password", false);
        }
        
        User user = userOptional.get();
        
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return new AuthResponseDto(null, "Invalid email or password", false);
        }
        
        if (!user.isEnabled()) {
            return new AuthResponseDto(null, "Account is disabled", false);
        }
        
        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername());
        
        // Create user info DTO
        UserInfoDto userInfo = new UserInfoDto(
            user.getId(),
            user.getEmail(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getRole()
        );
        
        AuthResponseDto response = new AuthResponseDto(token, "Login successful", true);
        response.setUser(userInfo);
        
        return response;
    }
} 