package com.att.tdp.popcorn_palace;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;

public class BookingControllerTest {

    static Long movieId;
    static Long showtimeId;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;

        // Create a Movie
        String movieJson = """
            {
              "title": "Booking Test",
              "genre": "Action",
              "duration": 120,
              "rating": 8.5,
              "releaseYear": 2023
            }
        """;

        movieId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(movieJson)
                .when()
                .post("/movies")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        // Create a Showtime
        String now = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String later = LocalDateTime.now().plusDays(1).plusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        String showtimeJson = """
            {
              "startTime": "%s",
              "endTime": "%s",
              "theater": "Booking Theater",
              "price": 30.0,
              "movie": { "id": %d }
            }
        """.formatted(now, later, movieId);

        showtimeId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(showtimeJson)
                .when()
                .post("/showtimes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    @Test
    public void testValidBookingWithShowtimeId() {
        String bookingJson = """
            {
              "showtime": { "id": %d },
              "seats": [5, 6],
              "customerName": "Jane Doe"
            }
        """.formatted(showtimeId);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/bookings")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("seats.size()", is(2))
                .body("customerName", equalTo("Jane Doe"));
    }
}
