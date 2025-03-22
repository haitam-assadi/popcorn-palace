package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Booking;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.repository.BookingRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public Booking createBooking(Long showtimeId, List<Integer> seats, String customerName) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // Check for seat conflicts
        List<Booking> existing = bookingRepository.findByShowtimeId(showtimeId);
        for (Booking b : existing) {
            for (Integer seat : b.getSeats()) {
                if (seats.contains(seat)) {
                    throw new RuntimeException("One or more seats already booked.");
                }
            }
        }

        Booking booking = Booking.builder()
                .showtime(showtime)
                .seats(seats)
                .customerName(customerName)
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
