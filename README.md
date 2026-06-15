# B&B Backend

Spring Boot backend for the B&B burger ordering application.

## Stack
- Spring Boot
- Java 21
- PostgreSQL 16

## Run

```bash
mvn spring-boot:run
```

Or via Docker:

```bash
docker build -t bnb-backend .
docker run -p 8080:8080 \
  -e BNB_DB_URL=jdbc:postgresql://host.docker.internal:5432/bnb \
  -e BNB_DB_USERNAME=bnb_user \
  -e BNB_DB_PASSWORD=bnb_password \
  bnb-backend
```

## Key API routes
- `GET /api/customers?status=ACTIVE&search=`
- `POST /api/customers`
- `PUT /api/customers/{id}`
- `PUT /api/customers/{id}/status`
- `POST /api/customers/{id}/order-lines`
- `DELETE /api/customers/{id}/order-lines/{lineId}`
- `GET /api/items`
- `POST /api/items`
- `PUT /api/items/{id}`
- `GET /api/categories`
- `POST /api/categories`
