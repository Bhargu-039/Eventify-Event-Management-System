# Eventify — Event Management System

Eventify is a Spring Boot web application for discovering and managing events. It uses Java, Spring MVC, Spring Data JPA, Thymeleaf, and MySQL.

## Features

- Event discovery and search
- Event creation and management
- User sign-up and sign-in
- Attendee registration and attendee views
- Check-in and ticket pages
- Server-rendered pages using Thymeleaf

## Tech stack

- Java 17
- Spring Boot
- Spring MVC and Thymeleaf
- Spring Data JPA / Hibernate
- MySQL
- Maven

## Prerequisites

- JDK 17 or compatible JDK supported by the Spring Boot version in `pom.xml`
- MySQL Server
- A database named `eventdb`

## Run locally

1. Create the database in MySQL:

   ```sql
   CREATE DATABASE eventdb;
   ```

2. Configure database credentials using environment variables `DB_USERNAME` and `DB_PASSWORD`. The username defaults to `root`; set `DB_PASSWORD` to your local MySQL password. These values are read by `src/main/resources/application.properties`.

   Windows PowerShell example (for the current terminal only):

   ```powershell
   $env:DB_USERNAME="root"
   $env:DB_PASSWORD="your-local-mysql-password"
   .\mvnw.cmd spring-boot:run
   ```

   On macOS/Linux:

   ```bash
   export DB_USERNAME=root
   export DB_PASSWORD='your-local-mysql-password'
   ./mvnw spring-boot:run
   ```

3. Open `http://localhost:8080` in your browser.

If you use Eclipse, import the extracted repository as **Existing Maven Projects**, then configure the same environment variables in the Spring Boot Run Configuration.

## Database note

Hibernate is configured with `ddl-auto=update`, so it can update tables based on the JPA entities. Back up important data before changing schema or application versions.

## GitHub notes

- Generated Maven `target/` files are intentionally excluded.
- Database passwords are not stored in the repository. Configure them locally through environment variables.
- Do not commit real passwords, API keys, or private data.

## Project structure

```text
src/
  main/
    java/com/event/eventmanagement/
      controller/
      model/
      repository/
    resources/
      static/
      templates/
      application.properties
  test/
```

## License

No license is specified yet. Add a license file if you want others to reuse or redistribute this project.
