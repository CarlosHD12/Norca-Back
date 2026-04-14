package com.upc.ep.Repositorio;
import com.upc.ep.Entidades.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepos extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}