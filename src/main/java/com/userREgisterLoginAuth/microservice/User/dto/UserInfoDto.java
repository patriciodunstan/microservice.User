package com.userREgisterLoginAuth.microservice.User.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDto {
    private String id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String role;
} 