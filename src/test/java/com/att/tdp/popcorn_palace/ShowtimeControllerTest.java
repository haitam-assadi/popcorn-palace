package com.att.tdp.popcorn_palace;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;

public class ShowtimeControllerTest {

    private static final Long VALID_MOVIE_ID = 1L; // Make sure movie with this ID exists

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Test
    public void testAddShowtime() {
        String start = formatDate(LocalDateTime.now().plusDays(1));
        String end = formatDate(LocalDateTime.now().plusDays(1).plusHours(2));

        String json = """
        {
            "startTime": "%s",
            "endTime": "%s",
            "theater": "Theater Test",
            "price": 39.99,
            "movie": { "id": %d }
        }
        """.formatted(start, end, VALID_MOVIE_ID);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/showtimes")
                .then()
                .statusCode(201)
                .body("theater", equalTo("Theater Test"))
                .body("price", equalTo(39.99F));
    }

    @Test
    public void testGetAllShowtimes() {
        RestAssured.given()
                .when()
                .get("/showtimes")
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    @Test
    public void testGetShowtimeById_NotFound() {
        RestAssured.given()
                .when()
                .get("/showtimes/999999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testUpdateShowtime() {
        // First, add a showtime
        String start = formatDate(LocalDateTime.now().plusDays(2));
        String end = formatDate(LocalDateTime.now().plusDays(2).plusHours(1));

        String requestBody = """
        {
            "startTime": "%s",
            "endTime": "%s",
            "theater": "Update Theater",
            "price": 20.0,
            "movie": { "id": %d }
        }
        """.formatted(start, end, VALID_MOVIE_ID);

        int showtimeId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/showtimes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");

        // Update it
        String updatedRequest = """
        {
            "startTime": "%s",
            "endTime": "%s",
            "theater": "Updated Theater",
            "price": 55.0,
            "movie": { "id": %d }
        }
        """.formatted(start, end, VALID_MOVIE_ID);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updatedRequest)
                .when()
                .put("/showtimes/" + showtimeId)
                .then()
                .statusCode(200)
                .body("theater", equalTo("Updated Theater"))
                .body("price", equalTo(55.0F));
    }

    @Test
    public void testDeleteShowtime() {
        // Create a showtime to delete
        String start = formatDate(LocalDateTime.now().plusDays(3));
        String end = formatDate(LocalDateTime.now().plusDays(3).plusHours(2));

        String json = """
        {
            "startTime": "%s",
            "endTime": "%s",
            "theater": "Delete Theater",
            "price": 15.0,
            "movie": { "id": %d }
        }
        """.formatted(start, end, VALID_MOVIE_ID);

        int showtimeId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/showtimes")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getInt("id");

        // Now delete it
        RestAssured.given()
                .when()
                .delete("/showtimes/" + showtimeId)
                .then()
                .statusCode(204);
    }
}
