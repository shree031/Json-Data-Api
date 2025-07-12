# 📦 JSON Dataset API

A Spring Boot backend service that allows:

- Inserting JSON records into a relational database under dataset names.
- Querying dataset records with dynamic `groupBy` and `sortBy` operations.

---

## 🚀 Features

- ✅ Insert arbitrary JSON records per dataset
- ✅ Group records by any JSON field
- ✅ Sort records by any JSON field (asc/desc)
- ✅ Clean DTO-entity mapping
- ✅ Robust exception handling
- ✅ Swagger API documentation
- ✅ Follows Java coding standards (no wildcard imports, service interfaces, DTO usage)

---

## 📁 Project Structure

```
com.shree031.jsondatasetapi
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
│   └── impl
└── config
```

---

## 🛠 Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Jackson for JSON processing
- Lombok (with `@Data`, `@RequiredArgsConstructor`)
- Swagger (Springdoc OpenAPI)

---

## 🧪 How to Run

### Prerequisites

- Java 17+
- Maven
- PostgreSQL

### Steps

```bash
git clone https://github.com/shree031/Json-Dataset-Api.git
cd json-dataset-api
```

1. Set up your database (PostgreSQL).
2. Update the `application.properties` file with your DB credentials.
3. Run the project:

```bash
./mvnw spring-boot:run
```

---

## 📘 API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 📡 API Endpoints

### 1️⃣ Insert Record

**POST** `/api/dataset/{datasetName}/record`

**Request Body:**

```json
{
  "id": 1,
  "name": "John Doe",
  "age": 30,
  "department": "Engineering"
}
```

**Response:**

```json
{
  "message": "Record added successfully",
  "dataset": "employee_dataset",
  "recordId": 1
}
```

---

### 2️⃣ Query with Group By

**GET** `/api/dataset/{datasetName}/query?groupBy=department`

**Response:**

```json
{
  "groupedRecords": {
    "Engineering": [
      { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" }
    ],
    "Marketing": [
      { "id": 2, "name": "Alice", "age": 27, "department": "Marketing" }
    ]
  }
}
```

---

### 3️⃣ Query with Sort By

**GET** `/api/dataset/{datasetName}/query?sortBy=age&order=asc`

**Response:**

```json
{
  "sortedRecords": [
    { "id": 2, "name": "Alice", "age": 27, "department": "Marketing" },
    { "id": 1, "name": "John Doe", "age": 30, "department": "Engineering" }
  ]
}
```

---

## ⚠️ Error Handling

Standardized error response:

```json
{
  "error": "Invalid groupBy/sortBy field: xyz"
}
```

Handled via `@ControllerAdvice`.

---

## 🧪 Test via Postman or Swagger

Use either:

- Postman collections
- Swagger at: `http://localhost:8080/swagger-ui.html`

---

## 📝 Notes

- DTOs are used for input/output, entities are not exposed directly.
- Service interfaces are defined and implemented separately.
- All JSON fields are stored as a string (`jsonb` column in PostgreSQL).
- Exception handling is implemented using a global handler.

---

## ✨ Author

Made with ❤️ by Shreedevi Patil


