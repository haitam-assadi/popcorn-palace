package com.att.tdp.popcorn_palace;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShowtimeControllerTest {
    static Long movieId;

    @LocalServerPort
    private int port;

    @BeforeAll
    public void setup() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost/";

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
        """.formatted(start, end, movieId);

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
        """.formatted(start, end, movieId);

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
        """.formatted(start, end, movieId);

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
        """.formatted(start, end, movieId);

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
