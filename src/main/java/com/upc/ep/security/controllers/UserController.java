package com.upc.ep.security.controllers;
import com.upc.ep.security.dtos.UserResponseDTO;
import com.upc.ep.security.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Norca/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> listarUsuarios() {
        return ResponseEntity.ok(userService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.obtenerPorId(id));
    }

    @PutMapping("/{userId}/role/{roleId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> assignRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {

        System.out.println("ENTRO AL CONTROLADOR");

        userService.assignRole(userId, roleId);

        return ResponseEntity.ok("Rol asignado correctamente");
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> desactivar(@PathVariable Long id) {
        userService.desactivar(id);
        return ResponseEntity.ok("Usuario desactivado correctamente");
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> activar(@PathVariable Long id) {
        userService.activar(id);
        return ResponseEntity.ok("Usuario activado correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        userService.eliminar(id);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }
}