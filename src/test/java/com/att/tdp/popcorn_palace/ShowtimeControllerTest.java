package com.att.tdp.popcorn_palace;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.*;

public class ShowtimeControllerTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    @Test
    public void testAddShowtime_success() {
        String now = formatDate(LocalDateTime.now().plusDays(1));
        String later = formatDate(LocalDateTime.now().plusDays(1).plusHours(2));

        String json = """
            {
              "startTime": "%s",
              "endTime": "%s",
              "theater": "Theater A",
              "price": 30.0,
              "movie": { "id": 1 }
            }
        """.formatted(now, later);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/showtimes")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("theater", equalTo("Theater A"))
                .body("price", equalTo(30.0f));
    }

    @Test
    public void testAddShowtime_invalidMovieId() {
        String now = formatDate(LocalDateTime.now().plusDays(2));
        String later = formatDate(LocalDateTime.now().plusDays(2).plusHours(1));

        String json = """
            {
              "startTime": "%s",
              "endTime": "%s",
              "theater": "Theater B",
              "price": 25.0,
              "movie": { "id": 9999 }
            }
        """.formatted(now, later);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(json)
                .when()
                .post("/showtimes")
                .then()
                .statusCode(404);
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
    public void testGetShowtimeById_notFound() {
        RestAssured.given()
                .when()
                .get("/showtimes/999999")
                .then()
                .statusCode(404);
    }
}
