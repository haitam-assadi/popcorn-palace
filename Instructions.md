# 📖 Instructions

Welcome to the **Popcorn Palace** backend system!  
This guide will walk you through setting up, running, and testing the project both automatically and manually.

---

## ✅ Prerequisites

Before starting, make sure you have the following installed:

- [Java 21+](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- [Git](https://git-scm.com/) _(for version control)_
- [Postman](https://www.postman.com/) _(for manual API testing)_

---

## ⚙️ Setup Instructions

### 1. Clone the Repository

If you haven’t already:

```bash
git clone <your-repository-url>
cd popcorn-palace
```

### 2. Start PostgreSQL with Docker Compose

Make sure Docker is running, then launch the database with:

```bash
docker-compose up -d
```

> You can confirm it’s working by running:
```bash
docker ps
```

---

## 🚀 Build & Run the Application

### 3. Build the Project

Use Maven wrapper to build the project:

```bash
./mvnw clean install
```

### 4. Run the Spring Boot Application

Run the application via your IDE (like IntelliJ)  
**or** use the terminal:

```bash
./mvnw spring-boot:run
```

> The application will start on:  
> 🔗 `http://localhost:8080`

---

## 🧪 Run Automated Tests

To run all tests:

```bash
./mvnw test
```

> ✅ Make sure your database is running before executing the tests.

---

## 🔍 Manual Testing with Postman

### Base URL

```
http://localhost:8080
```

### Example Requests

#### 🎬 Add a Movie

`POST /movies`

```json
{
  "title": "Inception",
  "genre": "Sci-Fi",
  "duration": 148,
  "rating": 8.8,
  "releaseYear": 2010
}
```

#### 🕒 Add a Showtime

`POST /showtimes`

```json
{
  "startTime": "2025-03-30T18:00:00",
  "endTime": "2025-03-30T20:30:00",
  "theater": "Main Theater",
  "price": 35.0,
  "movie": { "id": 1 }
}
```

#### 🎟️ Create a Booking

`POST /bookings`

```json
{
  "showtimeId": 1,
  "seats": [3, 4, 5],
  "customerName": "Alice Johnson"
}
```

> Refer to [README.md](./README.md) for full endpoint details.

---

## 🧼 Clean Up Docker

To stop and remove containers:

```bash
docker-compose down
```

To also remove volumes:

```bash
docker-compose down -v
```

---

## 📦 Package the App (JAR)

To create a `.jar` file:

```bash
./mvnw clean package
```

Run it with:

```bash
java -jar target/popcorn-palace-0.0.1-SNAPSHOT.jar
```

---

## 📌 Notes

- Ensure nothing else is using port `8080`
- Database is PostgreSQL, managed via `docker-compose`
- Credentials/configs are in `application.properties`

---

Happy coding! 🎬🍿
