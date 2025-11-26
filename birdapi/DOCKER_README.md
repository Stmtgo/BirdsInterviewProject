# Bird API - Docker Deployment Guide

This guide explains how to build and run the Bird API using Docker Compose.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)
- Java 11 (for local development)
- Maven 3.9+ (for local development)

## Technology Stack

- **Java**: 11
- **Spring Boot**: 2.7.18
- **Database**: PostgreSQL 16
- **Build Tool**: Maven
- **Packaging**: WAR (executable)

## Project Structure

```
birdapi/
├── docker-compose.yml       # Docker Compose configuration
├── Dockerfile               # Multi-stage Docker build configuration
├── pom.xml                  # Maven configuration
├── API_DOCUMENTATION.md     # REST API documentation
├── src/
│   ├── main/
│   │   ├── java/com/demoapp/birdapi/
│   │   │   ├── controller/  # REST controllers
│   │   │   ├── service/     # Business logic
│   │   │   ├── repository/  # Data access
│   │   │   ├── model/       # JPA entities
│   │   │   ├── dto/         # Data transfer objects
│   │   │   ├── mapper/      # MapStruct mappers
│   │   │   └── exception/   # Exception handlers
│   │   └── resources/
│   │       └── application.properties
│   └── test/                # Unit and integration tests
└── target/                  # Build output
```

## Quick Start

### 1. Start the Application

```bash
docker-compose up --build
```

This command will:
- Build the Spring Boot application using Maven (multi-stage build)
- Start PostgreSQL 16 database
- Start the Bird API application
- Expose the API on `http://localhost:8080`

### 2. Access the API

Once the services are running, you can access the API at:
- **Base URL**: `http://localhost:8080`
- **Birds Endpoint**: `http://localhost:8080/api/birds`
- **Sightings Endpoint**: `http://localhost:8080/api/sightings`
- **Health Check**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`

### 3. Stop the Application

To stop all services:

```bash
docker-compose down
```

To stop and remove all data (including database):

```bash
docker-compose down -v
```

## Docker Configuration

### Dockerfile (Multi-stage Build)

The project uses a multi-stage Docker build:

**Build Stage:**
- Uses `maven:3.9-eclipse-temurin-11` image
- Compiles the application and creates a WAR file
- Skips tests during Docker build

**Run Stage:**
- Uses `eclipse-temurin:11-jre-alpine` (lightweight JRE)
- Copies `birdapi.war` from build stage
- Exposes port 8080
- Runs as executable WAR

### Docker Compose Services

#### PostgreSQL Database
- **Image**: postgres:16-alpine
- **Container Name**: birdapi-postgres
- **Port**: 5432:5432
- **Database Name**: birddb
- **Username**: birduser
- **Password**: birdpass
- **Data Volume**: postgres-data (persisted)
- **Health Check**: pg_isready command

#### Bird API Application
- **Container Name**: birdapi-app
- **Build**: Multi-stage from local Dockerfile
- **Port**: 8080:8080
- **Dependencies**: PostgreSQL (waits for health check)
- **Network**: birdapi-network (bridge)

## Environment Variables

The application supports the following environment variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | jdbc:postgresql://localhost:5432/birds | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | postgres | Database username |
| `SPRING_DATASOURCE_PASSWORD` | postgress | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | update | Hibernate DDL strategy |
| `SPRING_JPA_SHOW_SQL` | true | Show SQL queries in logs |

### Docker Compose Environment (Overrides)

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/birddb
  SPRING_DATASOURCE_USERNAME: birduser
  SPRING_DATASOURCE_PASSWORD: birdpass
  SPRING_JPA_HIBERNATE_DDL_AUTO: update
  SPRING_JPA_SHOW_SQL: "true"
```

## Useful Commands

### View Logs

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f birdapi
docker-compose logs -f postgres
```

### Restart Services

```bash
docker-compose restart
```

### Rebuild Without Cache

```bash
docker-compose build --no-cache
docker-compose up
```

### Access PostgreSQL Shell

```bash
docker exec -it birdapi-postgres psql -U birduser -d birddb
```

### List Running Containers

```bash
docker-compose ps
```

### Run Tests Locally

```bash
mvn test
```

### Generate Javadoc

```bash
mvn javadoc:javadoc
# Output: target/reports/apidocs/index.html
```

## Troubleshooting

### Port Already in Use

If port 8080 or 5432 is already in use, modify the ports in `docker-compose.yml`:

```yaml
services:
  birdapi:
    ports:
      - "8081:8080"  # Change external port
  
  postgres:
    ports:
      - "5433:5432"  # Change external port
```

### Database Connection Issues

1. Check if PostgreSQL is healthy:
   ```bash
   docker-compose ps
   ```

2. Check PostgreSQL logs:
   ```bash
   docker-compose logs postgres
   ```

3. Ensure the application waits for the database health check

### Application Build Fails

1. Clean local Maven cache:
   ```bash
   mvn clean
   ```

2. Rebuild without cache:
   ```bash
   docker-compose build --no-cache
   ```

### WAR File Not Found

Ensure the `pom.xml` has the correct configuration:
- `<packaging>war</packaging>`
- `<finalName>birdapi</finalName>`

The build produces `target/birdapi.war`.

## Development vs Production

### Development Mode (Current Setup)
- Uses `spring.jpa.hibernate.ddl-auto=update`
- Shows SQL logs with formatted output
- Debug logging enabled for Spring
- Data persists in Docker volume
- Actuator endpoints exposed: health, info, env, metrics, beans

### Production Recommendations
1. Change `ddl-auto` to `validate` or `none`
2. Disable SQL logging
3. Set `logging.level.org.springframework=INFO`
4. Use proper database migrations (Flyway/Liquibase)
5. Use secrets management for passwords
6. Configure health checks and monitoring
7. Limit Actuator endpoint exposure

## API Examples

### Create a Bird

```bash
curl -X POST http://localhost:8080/api/birds \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Blue Jay",
    "color": "Blue",
    "weight": 0.085,
    "height": 30.0
  }'
```

### Get All Birds (Paginated)

```bash
curl "http://localhost:8080/api/birds?page=0&size=5&sort=name,asc"
```

### Search Birds

```bash
curl "http://localhost:8080/api/birds/search?name=Jay&color=Blue"
```

### Create a Sighting

```bash
curl -X POST http://localhost:8080/api/sightings \
  -H "Content-Type: application/json" \
  -d '{
    "birdId": 1,
    "location": "Central Park, New York",
    "dateTime": "2025-11-26T10:30:00"
  }'
```

### Search Sightings

```bash
curl "http://localhost:8080/api/sightings/search?birdName=Blue Jay&location=Park&fromDate=2025-01-01T00:00:00&toDate=2025-12-31T23:59:59"
```

### Delete a Bird

```bash
curl -X DELETE http://localhost:8080/api/birds/1
```

## Related Documentation

- [API_DOCUMENTATION.md](API_DOCUMENTATION.md) - Full REST API reference
- [Javadoc](target/reports/apidocs/index.html) - Generated API documentation

## Additional Resources

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)
- [Spring Boot 2.7.x Documentation](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/)
