package com.att.tdp.popcorn_palace;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

public class MovieControllerTest {

    @BeforeAll
    public static void setup() {
        // Point to your local server
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    public void testAddMovie_success() {
        String movieJson = """
            {
              "title": "The Matrix",
              "genre": "Sci-Fi",
              "duration": 136,
              "rating": 8.7,
              "releaseYear": 1999
            }
        """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(movieJson)
                .when()
                .post("/movies")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("title", equalTo("The Matrix"))
                .body("genre", equalTo("Sci-Fi"));
    }

    @Test
    public void testGetAllMovies() {
        RestAssured.given()
                .when()
                .get("/movies")
                .then()
                .statusCode(anyOf(is(200), is(204))); // 204 if no content
    }

    @Test
    public void testGetMovieById_notFound() {
        RestAssured.given()
                .when()
                .get("/movies/999999")
                .then()
                .statusCode(404);
    }

    @Test
    public void testAddMovie_invalidRating() {
        String invalidMovieJson = """
            {
              "title": "Invalid Movie",
              "genre": "Drama",
              "duration": 120,
              "rating": 11.0,
              "releaseYear": 2020
            }
        """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(invalidMovieJson)
                .when()
                .post("/movies")
                .then()
                .statusCode(400); // Validation error
    }

    @Test
    public void testAddMovie_missingTitle() {
        String movieJson = """
        {
          "title": "",
          "genre": "Action",
          "duration": 100,
          "rating": 7.5,
          "releaseYear": 2005
        }
    """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(movieJson)
                .when()
                .post("/movies")
                .then()
                .statusCode(400)
                .body("title", equalTo("Title cannot be empty"));
    }

    @Test
    public void testAddMovie_invalidReleaseYear() {
        String movieJson = """
        {
          "title": "Oldie",
          "genre": "History",
          "duration": 90,
          "rating": 7.2,
          "releaseYear": 1800
        }
    """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(movieJson)
                .when()
                .post("/movies")
                .then()
                .statusCode(400)
                .body("releaseYear", equalTo("Release year must be valid"));
    }

    @Test
    public void testAddMovie_zeroDuration() {
        String movieJson = """
        {
          "title": "Zero Time",
          "genre": "Short",
          "duration": 0,
          "rating": 5.0,
          "releaseYear": 2010
        }
    """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(movieJson)
                .when()
                .post("/movies")
                .then()
                .statusCode(400)
                .body("duration", equalTo("Duration must be at least 1 minute"));
    }

    @Test
    public void testUpdateMovie_success() {
        // First create a movie
        String movieJson = """
        {
          "title": "Original",
          "genre": "Drama",
          "duration": 100,
          "rating": 7.0,
          "releaseYear": 2015
        }
    """;

        Long movieId = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(movieJson)
                .when()
                .post("/movies")
                .then()
                .statusCode(201)
                .extract()
                .jsonPath()
                .getLong("id");

        // Now update it
        String updatedMovieJson = """
        {
          "title": "Updated Title",
          "genre": "Updated Genre",
          "duration": 120,
          "rating": 8.5,
          "releaseYear": 2020
        }
    """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(updatedMovieJson)
                .when()
                .put("/movies/" + movieId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"))
                .body("duration", equalTo(120));
    }

    @Test
    public void testDeleteMovie_notFound() {
        RestAssured.given()
                .when()
                .delete("/movies/999999")
                .then()
                .statusCode(404);
    }

}
