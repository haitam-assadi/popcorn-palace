package com.att.tdp.popcorn_palace;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class BookingControllerTest {

    private static final Long EXISTING_SHOWTIME_ID = 1L; // Replace with actual ID if needed

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void testCreateValidBooking() {
        String bookingJson = """
            {
              "showtime": { "id": %d },
              "seats": [10, 11],
              "customerName": "John Doe"
            }
        """.formatted(EXISTING_SHOWTIME_ID);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/bookings")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("customerName", equalTo("John Doe"))
                .body("seats.size()", equalTo(2));
    }

    @Test
    public void testBookingMissingCustomer_shouldFail() {
        String bookingJson = """
            {
              "showtime": { "id": %d },
              "seats": [1, 2]
            }
        """.formatted(EXISTING_SHOWTIME_ID);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/bookings")
                .then()
                .statusCode(400)
                .body(containsString("customerName"));
    }

    @Test
    public void testBookingInvalidShowtime_shouldFail() {
        String bookingJson = """
            {
              "showtime": { "id": 9999 },
              "seats": [1],
              "customerName": "Test User"
            }
        """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(bookingJson)
                .when()
                .post("/bookings")
                .then()
                .statusCode(400)
                .body(containsString("not found")); // Adjust depending on your error message
    }
}
