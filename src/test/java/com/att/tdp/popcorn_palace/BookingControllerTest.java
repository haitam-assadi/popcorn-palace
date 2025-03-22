package com.att.tdp.popcorn_palace;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class BookingControllerTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void testCreateBooking_success() {
        // Make sure movie with ID 1 and showtime with ID 1 exist before running this
        String bookingJson = """
            {
              "showtime": {
                "id": 1
              },
              "customerName": "John Doe",
              "seats": [6, 7]
            }
        """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/bookings")
                .then()
                .statusCode(201)
                .body(containsString("Booking successful"));
    }

    @Test
    public void testCreateBooking_conflictOnDuplicateSeats() {
        // Attempting to book the same seats again should result in 409
        String bookingJson = """
            {
              "showtime": {
                "id": 1
              },
              "customerName": "Jane Doe",
              "seats": [3, 4]
            }
        """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/bookings")
                .then()
                .statusCode(409)
                .body(containsString("already booked"));
    }

    @Test
    public void testGetAllBookings() {
        RestAssured.given()
                .when()
                .get("/bookings")
                .then()
                .statusCode(200)
                .body("$", not(empty()));
    }

    @Test
    public void testGetBookingById_notFound() {
        RestAssured.given()
                .when()
                .get("/bookings/9999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDeleteBooking_success() {
        // First create a booking
        String bookingJson = """
            {
              "showtime": {
                "id": 1
              },
              "customerName": "Mark Smith",
              "seats": [5]
            }
        """;

        // Create booking
        int bookingId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .post("/bookings")
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Now delete it
        RestAssured.given()
                .delete("/bookings/" + bookingId)
                .then()
                .statusCode(204);
    }
}
