# Bird API Documentation

This document describes the REST API endpoints for the Bird Sighting Application.

## Base URL

```
http://localhost:8080/api
```

---

## Birds API

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/birds` | Get all birds (paginated) |
| GET | `/birds/{id}` | Get a bird by ID |
| GET | `/birds/search` | Search birds by name and/or color |
| POST | `/birds` | Create a new bird |
| PUT | `/birds/{id}` | Update an existing bird |
| DELETE | `/birds/{id}` | Delete a bird |

---

### Get All Birds

Retrieves a paginated list of all birds.

**Request:**
```http
GET /api/birds?page=0&size=5&sort=id,asc
```

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | integer | 0 | Page number (zero-based) |
| size | integer | 5 | Number of items per page |
| sort | string | id,asc | Sort field and direction |

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "name": "Sparrow",
      "color": "Brown",
      "weight": 0.03,
      "height": 15.0
    },
    {
      "id": 2,
      "name": "Blue Jay",
      "color": "Blue",
      "weight": 0.1,
      "height": 25.0
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalElements": 2,
  "totalPages": 1,
  "last": true,
  "first": true,
  "size": 5,
  "number": 0,
  "numberOfElements": 2,
  "empty": false
}
```

---

### Get Bird by ID

Retrieves a single bird by its ID.

**Request:**
```http
GET /api/birds/1
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Sparrow",
  "color": "Brown",
  "weight": 0.03,
  "height": 15.0
}
```

**Error Response:** `404 Not Found`
```json
{
  "status": 404,
  "message": "Bird not found with id: 1",
  "timestamp": "2025-11-26T10:30:00"
}
```

---

### Search Birds

Search for birds by name and/or color.

**Request:**
```http
GET /api/birds/search?name=Sparrow&color=Brown&page=0&size=5
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| name | string | No | Bird name (partial match, case-insensitive) |
| color | string | No | Bird color (exact match, case-insensitive) |
| page | integer | No | Page number (default: 0) |
| size | integer | No | Page size (default: 5) |
| sort | string | No | Sort field and direction (default: id,asc) |

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "name": "Sparrow",
      "color": "Brown",
      "weight": 0.03,
      "height": 15.0
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

---

### Create Bird

Creates a new bird.

**Request:**
```http
POST /api/birds
Content-Type: application/json

{
  "name": "Eagle",
  "color": "Brown",
  "weight": 4.5,
  "height": 90.0
}
```

**Request Body:**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | Cannot be blank |
| color | string | Yes | Cannot be blank |
| weight | number | Yes | Must be positive |
| height | number | Yes | Must be positive |

**Response:** `201 Created`
```json
{
  "id": 3,
  "name": "Eagle",
  "color": "Brown",
  "weight": 4.5,
  "height": 90.0
}
```

**Error Response:** `400 Bad Request`
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "weight": "Weight must be positive"
  },
  "timestamp": "2025-11-26T10:30:00"
}
```

---

### Update Bird

Updates an existing bird.

**Request:**
```http
PUT /api/birds/1
Content-Type: application/json

{
  "name": "House Sparrow",
  "color": "Brown",
  "weight": 0.035,
  "height": 16.0
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "House Sparrow",
  "color": "Brown",
  "weight": 0.035,
  "height": 16.0
}
```

**Error Response:** `404 Not Found`
```json
{
  "status": 404,
  "message": "Bird not found with id: 1",
  "timestamp": "2025-11-26T10:30:00"
}
```

---

### Delete Bird

Deletes a bird by ID.

**Request:**
```http
DELETE /api/birds/1
```

**Response:** `204 No Content`

**Error Response:** `404 Not Found`
```json
{
  "status": 404,
  "message": "Bird not found with id: 1",
  "timestamp": "2025-11-26T10:30:00"
}
```

---

## Sightings API

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/sightings` | Get all sightings (paginated) |
| GET | `/sightings/{id}` | Get a sighting by ID |
| GET | `/sightings/search` | Search sightings with filters |
| POST | `/sightings` | Create a new sighting |
| PUT | `/sightings/{id}` | Update an existing sighting |
| DELETE | `/sightings/{id}` | Delete a sighting |

---

### Get All Sightings

Retrieves a paginated list of all sightings.

**Request:**
```http
GET /api/sightings?page=0&size=5&sort=id,asc
```

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | integer | 0 | Page number (zero-based) |
| size | integer | 5 | Number of items per page |
| sort | string | id,asc | Sort field and direction |

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "birdId": 1,
      "location": "Central Park, New York",
      "dateTime": "2025-11-26T10:30:00",
      "bird": {
        "id": 1,
        "name": "Sparrow",
        "color": "Brown",
        "weight": 0.03,
        "height": 15.0
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 5,
  "number": 0
}
```

---

### Get Sighting by ID

Retrieves a single sighting by its ID.

**Request:**
```http
GET /api/sightings/1
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "birdId": 1,
  "location": "Central Park, New York",
  "dateTime": "2025-11-26T10:30:00",
  "bird": {
    "id": 1,
    "name": "Sparrow",
    "color": "Brown",
    "weight": 0.03,
    "height": 15.0
  }
}
```

**Error Response:** `404 Not Found`
```json
{
  "status": 404,
  "message": "Sighting not found with id: 1",
  "timestamp": "2025-11-26T10:30:00"
}
```

---

### Search Sightings

Search for sightings with various filters.

**Request:**
```http
GET /api/sightings/search?birdName=Sparrow&location=Park&fromDate=2025-01-01T00:00:00&toDate=2025-12-31T23:59:59
```

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| birdName | string | No | Bird name (exact match) |
| location | string | No | Location (partial match, case-insensitive) |
| fromDate | datetime | No | Start date/time (ISO 8601 format) |
| toDate | datetime | No | End date/time (ISO 8601 format) |
| page | integer | No | Page number (default: 0) |
| size | integer | No | Page size (default: 5) |
| sort | string | No | Sort field and direction (default: id,asc) |

**Date Format:** `yyyy-MM-dd'T'HH:mm:ss` (e.g., `2025-11-26T10:30:00`)

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": 1,
      "birdId": 1,
      "location": "Central Park, New York",
      "dateTime": "2025-11-26T10:30:00",
      "bird": {
        "id": 1,
        "name": "Sparrow",
        "color": "Brown",
        "weight": 0.03,
        "height": 15.0
      }
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

---

### Create Sighting

Creates a new bird sighting.

**Request:**
```http
POST /api/sightings
Content-Type: application/json

{
  "birdId": 1,
  "location": "Central Park, New York",
  "dateTime": "2025-11-26T10:30:00"
}
```

**Request Body:**

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| birdId | integer | Yes | Must reference an existing bird |
| location | string | Yes | Cannot be blank |
| dateTime | datetime | Yes | Format: yyyy-MM-dd'T'HH:mm:ss |

**Response:** `201 Created`
```json
{
  "id": 1,
  "birdId": 1,
  "location": "Central Park, New York",
  "dateTime": "2025-11-26T10:30:00",
  "bird": {
    "id": 1,
    "name": "Sparrow",
    "color": "Brown",
    "weight": 0.03,
    "height": 15.0
  }
}
```

**Error Response:** `400 Bad Request`
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "birdId": "Bird ID is required",
    "location": "Location is required"
  },
  "timestamp": "2025-11-26T10:30:00"
}
```

**Error Response:** `404 Not Found` (when bird doesn't exist)
```json
{
  "status": 404,
  "message": "Bird not found with id: 999",
  "timestamp": "2025-11-26T10:30:00"
}
```

---

### Update Sighting

Updates an existing sighting.

**Request:**
```http
PUT /api/sightings/1
Content-Type: application/json

{
  "birdId": 1,
  "location": "Prospect Park, Brooklyn",
  "dateTime": "2025-11-26T14:00:00"
}
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "birdId": 1,
  "location": "Prospect Park, Brooklyn",
  "dateTime": "2025-11-26T14:00:00",
  "bird": {
    "id": 1,
    "name": "Sparrow",
    "color": "Brown",
    "weight": 0.03,
    "height": 15.0
  }
}
```

---

### Delete Sighting

Deletes a sighting by ID.

**Request:**
```http
DELETE /api/sightings/1
```

**Response:** `204 No Content`

**Error Response:** `404 Not Found`
```json
{
  "status": 404,
  "message": "Sighting not found with id: 1",
  "timestamp": "2025-11-26T10:30:00"
}
```

---

## Data Models

### Bird

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| name | String | Name of the bird species |
| color | String | Primary color of the bird |
| weight | Double | Weight in kilograms |
| height | Double | Height in centimeters |

### Sighting

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Unique identifier (auto-generated) |
| birdId | Long | Reference to the bird that was sighted |
| location | String | Location where the bird was sighted |
| dateTime | LocalDateTime | Date and time of the sighting |
| bird | Bird | Full bird details (included in response) |

---

## Error Responses

All error responses follow this format:

```json
{
  "status": 404,
  "message": "Resource not found",
  "timestamp": "2025-11-26T10:30:00",
  "errors": {}
}
```

### HTTP Status Codes

| Status Code | Description |
|-------------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 204 | No Content - Resource deleted successfully |
| 400 | Bad Request - Validation error or invalid request |
| 404 | Not Found - Resource not found |
| 500 | Internal Server Error - Server error |

---

## Pagination

All list endpoints support pagination with the following parameters:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | integer | 0 | Page number (zero-based) |
| size | integer | 5 | Number of items per page |
| sort | string | id,asc | Sort field and direction |

**Sort Format:** `field,direction` (e.g., `name,asc` or `dateTime,desc`)

**Paginated Response Structure:**
```json
{
  "content": [],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 100,
  "totalPages": 20,
  "last": false,
  "first": true,
  "size": 5,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 5,
  "empty": false
}
```
