package com.userREgisterLoginAuth.microservice.User.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String message;
    private boolean success;
    private UserInfoDto user;
    
    public AuthResponseDto(String token, String message, boolean success) {
        this.token = token;
        this.message = message;
        this.success = success;
    }
} 