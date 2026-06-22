package com.upc.ep.security.services;
import com.upc.ep.security.dtos.RegisterUserDTO;
import com.upc.ep.security.dtos.UserResponseDTO;
import com.upc.ep.security.entities.Role;
import com.upc.ep.security.entities.User;
import com.upc.ep.security.repositories.RoleRepository;
import com.upc.ep.security.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User save(User user) {

        Role defaultRole = roleRepository
                .findByNombre("ROLE_OPERADOR")
                .orElseThrow(() ->
                        new RuntimeException("Rol por defecto no encontrado"));

        user.setRole(defaultRole);
        user.setEnabled(true);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarTodos() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO obtenerPorId(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        return mapToResponse(user);
    }

    public void assignRole(Long userId, Long roleId) {

        System.out.println("ENTRO AL SERVICE");

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        System.out.println(auth);
        System.out.println(auth.getAuthorities());

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new RuntimeException("Rol no encontrado"));

        String usernameLogeado = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (user.getUsername().equals(usernameLogeado)
                && "ROLE_ADMIN".equals(user.getRole().getNombre())
                && !"ROLE_ADMIN".equals(role.getNombre())) {

            throw new IllegalArgumentException(
                    "No puedes quitarte tu propio rol ADMIN");
        }

        user.setRole(role);
    }

    public void activar(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        user.setEnabled(true);
    }

    public void desactivar(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        String usernameLogeado = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (user.getUsername().equals(usernameLogeado)) {
            throw new IllegalArgumentException(
                    "No puedes desactivar tu propio usuario");
        }

        user.setEnabled(false);
    }

    public void eliminar(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        String usernameLogeado = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        if (user.getUsername().equals(usernameLogeado)) {
            throw new IllegalArgumentException(
                    "No puedes eliminar tu propio usuario");
        }

        userRepository.delete(user);
    }

    private UserResponseDTO mapToResponse(User user) {

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole().getNombre())
                .enabled(user.getEnabled())
                .build();
    }
}