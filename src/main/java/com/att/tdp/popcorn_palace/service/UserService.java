package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Role;
import com.att.tdp.popcorn_palace.entity.User;
import com.att.tdp.popcorn_palace.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean isAdmin(String username){
        return userRepository.findByUsername(username)
                .map(user -> user.getRole() == Role.ADMIN)
                .orElse(false);
    }

    public Role getUserRole(String username) {
        return userRepository.findByUsername(username)
                .map(User::getRole)
                .orElse(null);
    }
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public boolean validatelogin(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && encoder.matches(password,user.get().getPassword());
    }
}
