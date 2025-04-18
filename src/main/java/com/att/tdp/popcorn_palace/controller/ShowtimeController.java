package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Role;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.service.ShowtimeService;
import com.att.tdp.popcorn_palace.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {

    private final ShowtimeService showtimeService;
    private final UserService userService;

    public ShowtimeController(ShowtimeService showtimeService, UserService userService) {
        this.showtimeService = showtimeService;
        this.userService = userService;
    }

    // Create a new showtime
    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@Valid @RequestBody Showtime showtime) {
        Showtime saved = showtimeService.addShowtime(showtime);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    // Get all showtimes
    @GetMapping
    public ResponseEntity<?> getAllShowtimes(@RequestParam String username) {
        Role role = userService.getUserRole(username);
        if(role == null || role !=Role.ADMIN){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("just admins can get show times");
        }
        List<Showtime> showtimes = showtimeService.getAllShowtimes();
        return ResponseEntity.ok(showtimes);
    }

    // Get a showtime by ID
    @GetMapping("/{id}")
    public ResponseEntity<Showtime> getShowtimeById(@PathVariable Long id) {
        return showtimeService.getShowtimeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete a showtime
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long id) {
        showtimeService.deleteShowtime(id);
        return ResponseEntity.noContent().build();
    }

    // Update a showtime
    @PutMapping("/{id}")
    public ResponseEntity<Showtime> updateShowtime(@PathVariable Long id, @Valid @RequestBody Showtime updatedShowtime) {
        Showtime result = showtimeService.updateShowtime(id, updatedShowtime);
        return ResponseEntity.ok(result);
    }
}
