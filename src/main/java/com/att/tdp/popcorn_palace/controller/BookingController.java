package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Booking;
import com.att.tdp.popcorn_palace.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Map<String, Object> request) {
        try {
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
