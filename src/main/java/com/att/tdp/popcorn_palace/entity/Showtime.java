package com.att.tdp.popcorn_palace.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @NotNull(message = "Theater is required")
    private String theater;

    @NotNull(message = "Price is required")
    private Double price;

    @ManyToOne
    @JoinColumn(name = "movie_id", nullable = false)
    @JsonIgnoreProperties("showtimes") // Prevent infinite loop
    private Movie movie;

    // Optional: add toString without movie field to avoid recursion in logs
    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", theater='" + theater + '\'' +
                ", price=" + price +
                '}';
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getTheater() {
        return theater;
    }

    public Double getPrice() {
        return price;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
