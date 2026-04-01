package com.stockflow_backend.controllers;

import com.stockflow_backend.dto.request.UserEntityRequestDTO;
import com.stockflow_backend.entities.AuthLoginRequestDTO;
import com.stockflow_backend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserEntityRequestDTO userEntityRequestDTO){
        authService.registerUser(userEntityRequestDTO);
        return new ResponseEntity<>("REGISTERED", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequestDTO authLoginRequestDTO){
        return new ResponseEntity<>(authService.login(authLoginRequestDTO), HttpStatus.OK);
    }

}
