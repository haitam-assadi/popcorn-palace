package com.att.tdp.popcorn_palace.controller;

import com.att.tdp.popcorn_palace.entity.Movie;
import com.att.tdp.popcorn_palace.entity.Role;
import com.att.tdp.popcorn_palace.repository.MovieRepository;
import com.att.tdp.popcorn_palace.security.JwtUtil;
import com.att.tdp.popcorn_palace.service.MovieService;
import com.att.tdp.popcorn_palace.service.UserService;
import jakarta.validation.Valid;
import org.apache.tomcat.util.http.parser.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.Authenticator;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final MovieService movieService;
    private final JwtUtil jwtUtil;

    public MovieController(MovieRepository movieRepository, UserService userService, MovieService movieService, JwtUtil jwtUtil) {
        this.movieRepository = movieRepository;
        this.userService = userService;
        this.movieService = movieService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> addMovie(@RequestBody Movie movie, @RequestHeader ("Authorization") String authHeader) {
        logger.info("Adding new movie: {}", movie.getTitle());

        String token = authHeader.replace("Bearer ", "");

        if(!jwtUtil.validateToken(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        String username = jwtUtil.extractUsername(token);
        Role role = userService.getUserRole(username);

        if (role == null || role != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("just admins can add movies");
        }

        return ResponseEntity.ok(movieService.saveMovie(movie));
    }

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movieList = movieRepository.findAll();
        if (movieList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(movieList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @Valid @RequestBody Movie movieDetails) {
        return movieRepository.findById(id)
                .map(movie -> {
                    movie.setTitle(movieDetails.getTitle());
                    movie.setGenre(movieDetails.getGenre());
                    movie.setDuration(movieDetails.getDuration());
                    movie.setRating(movieDetails.getRating());
                    movie.setReleaseYear(movieDetails.getReleaseYear());
                    logger.info("Updated movie with ID: {}", id);
                    return ResponseEntity.ok(movieRepository.save(movie));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        if (!movieRepository.existsById(id)) {
            logger.warn("Movie with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }
        movieRepository.deleteById(id);
        logger.info("Deleted movie with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
