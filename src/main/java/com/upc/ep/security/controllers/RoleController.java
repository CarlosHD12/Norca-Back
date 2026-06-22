package com.upc.ep.security.controllers;

import com.upc.ep.security.entities.Role;
import com.upc.ep.security.repositories.RoleRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Norca/roles")
@RequiredArgsConstructor
@Tag(name = "Roles")
public class RoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<List<Role>> listarRoles() {
        return ResponseEntity.ok(roleRepository.findAll());
    }
}