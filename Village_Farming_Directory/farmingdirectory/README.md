# Village Farming Directory

## 1. Project Overview

Village Farming Directory is a full-stack web application developed to manage farmers' personal information and farming details.

The application allows users to register, log in, manage farmer records, search farmers, and view farming statistics through a dashboard.

The project uses Java, Spring Boot, Thymeleaf, MySQL, HTML, CSS, and Bootstrap.

## 2. Objectives

- Maintain farmer information digitally.
- Store farming details in a MySQL database.
- Provide a user-friendly registration and login system.
- Manage farmer records using CRUD operations.
- Search farmers by name, village, and crops.
- Display farmer statistics on a dashboard.
- Provide a responsive and attractive user interface.

## 3. Technologies Used

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot | Main application framework |
| Spring MVC | Handling web requests |
| Spring Data JPA | Database operations |
| Hibernate | ORM and database table management |
| MySQL | Database |
| MySQL Workbench | Database management |
| HTML5 | Web page structure |
| CSS3 | Website styling |
| Bootstrap 5 | Responsive design |
| Thymeleaf | Dynamic HTML pages |
| Maven | Dependency management and project building |
| Eclipse IDE | Development environment |

## 4. Application Architecture

The application follows a simple three-part architecture:

Frontend → Controller → Repository → Database

### Frontend
HTML, CSS, Bootstrap, and Thymeleaf are used to create the website pages.

### Controller
Spring MVC Controller receives requests from the browser, processes the required operation, and returns the appropriate webpage.

### Repository
Spring Data JPA Repository communicates with the MySQL database to save, retrieve, update, and delete records.

### Database
MySQL stores farmer registration and farming information permanently.

## 5. Project Structure

Village-Farming-Directory/

- src/main/java/
  - com/example/farmingdirectory/
    - FarmingDirectoryApplication.java
    - backend/
      - controller/
        - FarmerController.java
      - model/
        - Farmer.java
      - repository/
        - FarmerRepository.java

- src/main/resources/
  - application.properties
  - templates/
    - login.html
    - register.html
    - dashboard.html
    - farmers.html
    - farmer-form.html
  - static/
    - css/
      - style.css

- pom.xml
- README.md

## 6. Database

Database name:

`village_farming_directory`

The application uses MySQL to store farmer information.

The Farmer table contains fields such as:

- ID
- Full Name
- Mobile Number
- Village
- Username
- Password
- Land Area
- Land Unit
- Crops
- Crop Season

Hibernate automatically creates or updates database tables using the JPA configuration.

## 7. Main Features

### Registration
Users can register with their personal details, username, password, and farming information.

### Login
Registered users can log in using their username and password.

### Logout
Users can log out and end their login session.

### Dashboard
The dashboard displays:

- Welcome message
- Total registered farmers
- Total farming land
- Navigation to application features

### Farmer Directory
Displays registered farmers in a table.

### Add Farmer
Allows adding a new farmer with personal and farming information.

### Edit Farmer
Allows updating existing farmer information.

### Delete Farmer
Allows removing farmer records from the database.

### Search
Search farmers by:

- Name
- Village
- Crops

## 8. CRUD Operations

CRUD stands for:

- Create – Add a farmer.
- Read – View farmer records.
- Update – Edit farmer information.
- Delete – Remove farmer records.

These operations are implemented using Spring MVC and Spring Data JPA.

## 9. How to Run the Project

### Requirements

- Java 21
- Eclipse IDE
- MySQL Server
- MySQL Workbench

### Steps

1. Install Java 21.
2. Install Eclipse IDE.
3. Install MySQL Server and MySQL Workbench.
4. Open the project in Eclipse.
5. Create the MySQL database.
6. Configure the database connection in `application.properties`.
7. Run `FarmingDirectoryApplication.java`.
8. Open a browser.
9. Visit:

   `http://localhost:8080`

10. Register an account and log in.

## 10. Application URL

Login page:

`http://localhost:8080`

Dashboard:

`http://localhost:8080/dashboard`

Farmer Directory:

`http://localhost:8080/farmers`

Add Farmer:

`http://localhost:8080/add-farmer`

## 11. Security and Validation

- Login credentials are checked against stored database records.
- Passwords are hashed using BCrypt.
- Login sessions are used to protect application pages.
- Required form fields are validated.
- Mobile numbers are validated.
- Usernames must be unique.
- Logout invalidates the current session.

## 12. Future Enhancements

- Farmer profile editing.
- Farmer profile photographs.
- Advanced crop filtering.
- Export farmer information to PDF or Excel.
- Improved reports and statistics.
- Admin management features.

## 13. Conclusion

Village Farming Directory provides a simple digital platform for managing farmer information and farming details.

The project demonstrates the use of Java, Spring Boot, Spring MVC, Spring Data JPA, Thymeleaf, Bootstrap, and MySQL in a full-stack web application.

It helps organize farming information and demonstrates database connectivity, authentication, session management, and CRUD operations.
