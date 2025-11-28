# Birds Management System

This repository contains two interconnected projects for managing bird sightings:

1. **Bird API** (`birdapi`) - A Spring Boot REST API backend
2. **Birds RCP** (`birdsrcp`) - An Eclipse RCP desktop client

---

## Bird API (Backend)

A Spring Boot REST API for managing birds and sightings.

### Technology Stack
- Java 11
- Spring Boot 2.7.18
- PostgreSQL 16
- Maven 3.9+
- MapStruct for DTO mapping

### Build & Run

#### Option 1: Docker (Recommended)

```bash
cd birdapi
docker-compose up --build
```

This starts both PostgreSQL and the API at `http://localhost:8080`.

#### Option 2: Local Development

1. Start PostgreSQL with database `birds` on port 5432
2. Build and run:

```bash
cd birdapi
./mvnw clean install
./mvnw spring-boot:run
```

### API Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /api/birds` | List all birds (paginated) |
| `GET /api/birds/search` | Search birds by name/color |
| `POST /api/birds` | Create a bird |
| `GET /api/sightings` | List all sightings |
| `POST /api/sightings` | Create a sighting |

See [API_DOCUMENTATION.md](birdapi/API_DOCUMENTATION.md) for full details.

### Run Tests

```bash
./mvnw test
```

---

## Birds RCP (Desktop Client)

An Eclipse RCP application that provides a desktop UI for the Bird API.

### Technology Stack
- Java 11
- Eclipse RCP (E4)
- SWT for UI
- Jackson for JSON processing

### Project Structure

| Plugin | Description |
|--------|-------------|
| `birds-logic-plugin` | Service layer and models |
| `birds-ui-plugin` | UI components (SWT parts) |
| `birds-rcp-project` | Main RCP application |

### Build & Run

1. Import all three plugins into Eclipse IDE (with PDE support)
2. Open `birds-rcp-project.product`
3. Click **Launch an Eclipse application**

Or run the pre-built application:

```bash
cd birdsrcp/birds-rcp-project/eclipse
./BirdsApp
```

### Prerequisites

- Eclipse IDE with PDE (Plugin Development Environment)
- Java 11 JDK
- **Bird API must be running** on `http://localhost:8080`

---

## Quick Start (Full System)

1. **Start the API:**
   ```bash
   cd birdapi
   docker-compose up --build
   ```

2. **Run the Desktop Client:**
   - Launch from Eclipse IDE, or
   - Run `BirdsApp` from `birdsrcp/birds-rcp-project/eclipse`

3. **Use the application** to add birds and record sightings!

---

## Documentation

- [API Documentation](birdapi/API_DOCUMENTATION.md)
- [Docker Guide](birdapi/DOCKER_README.md)
- [Test Documentation](birdapi/TEST_DOCUMENTATION.md)
