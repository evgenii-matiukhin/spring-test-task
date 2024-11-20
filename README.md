# Description   

This project implements a backend application for managing **Sections** and their **Geological Classes**. It includes RESTful APIs for CRUD operations, querying data, and importing/exporting information via XLS files. The application is built using **Spring Boot** and follows best practices for asynchronous operations and file processing.

---

## Requirements

# Backend Developer Test 2023

## Test Task for Backend Developer

1. Add REST CRUD API for **Sections** and **GeologicalClasses**. Each Section has the following structure:

    ```json
    {
        "name": "Section 1",
        "geologicalClasses": [
            { "name": "Geo Class 11", "code": "GC11" },
            { "name": "Geo Class 12", "code": "GC12" },
            ...
        ]
    }
    ```

2. Add the API `GET /sections/by-code?code=...` that returns a list of all Sections that have geologicalClasses with the specified code.

3. Add APIs for importing and exporting XLS files. Each XLS file contains headers and a list of sections with their geological classes. Example:

   | Section name | Class 1 name | Class 1 code | Class 2 name | Class 2 code | Class M name | Class M code |
       |--------------|--------------|--------------|--------------|--------------|--------------|--------------|
   | Section 1    | Geo Class 11 | GC11         | Geo Class 12 | GC12         | Geo Class 1M | GC1M         |
   | Section 2    | Geo Class 21 | GC21         | Geo Class 22 | GC22         |              |              |
   | Section 3    | Geo Class 31 | GC31         |              |              | Geo Class 3M | GC3M         |
   | Section N    | Geo Class N1 | GCN1         | Geo Class N2 | GCN2         | Geo Class NM | GCNM         |

   Files should be processed asynchronously, and results should be stored in the database.

    - **API POST /import**:
        - Accepts a file and returns the ID of the async job, launching the import process.
    - **API GET /import/{id}**:
        - Returns the result of the import job by Job ID (`"DONE"`, `"IN PROGRESS"`, `"ERROR"`).
    - **API GET /export**:
        - Initiates the export process and returns the ID of the async job.
    - **API GET /export/{id}**:
        - Returns the result of the export job by Job ID (`"DONE"`, `"IN PROGRESS"`, `"ERROR"`).
    - **API GET /export/{id}/file**:
        - Returns the exported file by Job ID (throws an exception if exporting is in progress).

## Requirements

- Technology stack: **Spring**, **Hibernate**, **Spring Data**, **Spring Boot**, **Gradle/Maven**.
- All data (except files) should be in JSON format.
- For export and import, use **Apache POI** for parsing.
- (Optional) **Basic Authorization** should be supported.

---

## Technology Stack

- **Java** (JDK 21+)
- **Spring Boot** for application framework
- **Spring Data JPA** for database interactions
- **Hibernate** for ORM
- **Apache POI** for Excel file parsing and generation
- **Gradle** for dependency management
- **H2** for database (configurable)

---

## Setup Instructions

### Prerequisites

- Java 21+
- Gradle 8.10+

### Steps to Run

1. Run `gradle build` to build the project.
2. Run `gradle bootRun` to start the application.
3. Access the application API at `http://localhost:8080/api/v1/` in your web browser.




