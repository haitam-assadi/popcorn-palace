package com.att.tdp.popcorn_palace.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Showtime is required")
    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    @NotEmpty(message = "At least one seat must be booked")
    @ElementCollection
    private List<Integer> seats;

    @NotEmpty(message = "Customer name is required")
    private String customerName;


    public static class BookingBuilder {
        private Showtime showtime;
        private List<Integer> seats;
        private String customerName;

        public BookingBuilder showtime(Showtime showtime) {
            this.showtime = showtime;
            return this;
        }

        public BookingBuilder seats(List<Integer> seats) {
            this.seats = seats;
            return this;
        }

        public BookingBuilder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Booking build() {
            return new Booking(showtime, customerName, seats);
        }
    }

    public static BookingBuilder builder() {
        return new BookingBuilder();
    }

    public Booking(Showtime showtime, String customerName, List<Integer> seats) {
        this.showtime = showtime;
        this.customerName = customerName;
        this.seats = seats;
    }

    public Booking() {
        // Required by JPA
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public List<Integer> getSeats() {
        return seats;
    }

    public void setSeats(List<Integer> seats) {
        this.seats = seats;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


}
