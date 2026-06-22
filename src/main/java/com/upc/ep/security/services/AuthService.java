package com.upc.ep.security.services;

import com.upc.ep.security.dtos.AuthRequestDTO;
import com.upc.ep.security.dtos.AuthResponseDTO;
import com.upc.ep.security.dtos.RegisterUserDTO;
import com.upc.ep.security.entities.User;
import com.upc.ep.security.repositories.UserRepository;
import com.upc.ep.security.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO login(AuthRequestDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .authorities(user.getRole().getNombre())
                        .build();

        String token = jwtUtil.generateToken(userDetails);

        return new AuthResponseDTO(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getRole().getNombre()
        );
    }

    public void register(RegisterUserDTO request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El usuario ya existe");
        }

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        userService.save(user);
    }
}