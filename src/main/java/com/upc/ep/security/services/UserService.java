package com.upc.ep.security.services;
import com.upc.ep.security.entities.User;
import com.upc.ep.security.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public Integer insertUserRol(Long user_id, Long rol_id) {
        try {
            return userRepository.insertUserRol(user_id, rol_id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}