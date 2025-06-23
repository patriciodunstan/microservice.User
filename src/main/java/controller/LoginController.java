package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.LoginRequest;
import service.TokenService;

@RestController
@RequestMapping("/api/v1/login")   
public class LoginController {

    private final TokenService tokenService;

    public LoginController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request)  {
        try {
              String token =  tokenService.authenticateUser(request);
              return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error logging in user");
        }
    }
    
}
