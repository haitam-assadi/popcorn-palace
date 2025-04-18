package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Role;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.service.UserService;
import com.att.tdp.popcorn_palace.security.JwtUtil;
import jakarta.validation.Valid;
import com.att.tdp.popcorn_palace.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<User> Register(@RequestBody User user) {
        Optional<User> existingUser = userService.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }
        User savedUser = userService.registerUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping
    public ResponseEntity<?> GetAllUsers(){
        List<User> Users = userService.getAllUsers();
        return ResponseEntity.ok(Users);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest){
        boolean valid = userService.validatelogin(loginRequest.getUsername(),loginRequest.getPassword());
        if(!valid){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid login");
        }

        String token = jwtUtil.generateToken(loginRequest.getUsername());
        Role role = userService.getUserRole(loginRequest.getUsername());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "role", role.name()
        ));
    }
}
