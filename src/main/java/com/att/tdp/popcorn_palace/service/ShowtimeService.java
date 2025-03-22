package com.att.tdp.popcorn_palace.service;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.entity.Showtime;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.repository.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    // Add a new showtime
    public Showtime addShowtime(Showtime showtime) {
        Long movieId = showtime.getMovie().getId();

        // 1. Check if the movie exists
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new IllegalArgumentException("Movie with ID " + movieId + " not found."));

        // 2. Check for overlapping showtimes
        List<Showtime> overlaps = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(), showtime.getStartTime(), showtime.getEndTime()
        );

        if (!overlaps.isEmpty()) {
            throw new IllegalArgumentException("Showtime overlaps with an existing showtime in the same theater.");
        }

        // 3. Set movie and save
        showtime.setMovie(movie);
        return showtimeRepository.save(showtime);
    }



    // Get all showtimes
    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    // Get a showtime by ID
    public Optional<Showtime> getShowtimeById(Long id) {
        return showtimeRepository.findById(id);
    }

    // Delete a showtime by ID
    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
    }

    // Update a showtime by ID
    public Showtime updateShowtime(Long id, Showtime updatedShowtime) {
        return showtimeRepository.findById(id).map(showtime -> {
            showtime.setTheater(updatedShowtime.getTheater());
            showtime.setStartTime(updatedShowtime.getStartTime());
            showtime.setEndTime(updatedShowtime.getEndTime());
            showtime.setPrice(updatedShowtime.getPrice());
            showtime.setMovie(updatedShowtime.getMovie());
            return showtimeRepository.save(showtime);
        }).orElseThrow(() -> new RuntimeException("Showtime not found with ID: " + id));
    }
}
