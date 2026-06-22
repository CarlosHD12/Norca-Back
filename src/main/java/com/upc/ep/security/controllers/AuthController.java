package com.upc.ep.security.controllers;
import com.upc.ep.security.dtos.AuthRequestDTO;
import com.upc.ep.security.dtos.AuthResponseDTO;
import com.upc.ep.security.dtos.RegisterUserDTO;
import com.upc.ep.security.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Norca/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterUserDTO request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
    }
}