package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Booking;
import com.att.tdp.popcorn_palace.entity.Role;
import com.att.tdp.popcorn_palace.security.JwtUtil;
import com.att.tdp.popcorn_palace.service.BookingService;
import com.att.tdp.popcorn_palace.service.UserService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public BookingController(BookingService bookingService, UserService userService, JwtUtil jwtUtil) {
        this.bookingService = bookingService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> request , @RequestHeader ("Authorization") String authHeader){
        try {
            String token = authHeader.replace("Bearer ", "");

            if(!jwtUtil.validateToken(token)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }

            String username = jwtUtil.extractUsername(token);
            Role role = userService.getUserRole(username);

            if(role == null || role != Role.CUSTOMER){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only Customers can book showtime");
            }

            Map<String, Object> showtimeMap = (Map<String, Object>) request.get("showtime");
            Long showtimeId = Long.valueOf(showtimeMap.get("id").toString());

            List<Integer> seats = ((List<?>) request.get("seats"))
                    .stream()
                    .map(seat -> Integer.parseInt(seat.toString()))
                    .toList();

            String customerName = request.get("customerName").toString();

            Booking booking = bookingService.createBooking(showtimeId, seats, customerName);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            e.printStackTrace(); // helpful for debugging
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }
}
