# Social Guardrails API

A Spring Boot microservice implementing guardrails for user and bot interactions using Redis for atomic operations and PostgreSQL for persistence.

---

## Overview

The system enforces strict constraints on interactions (likes, comments, bot replies) to prevent excessive or unsafe activity. It is designed to be stateless, with Redis handling all real-time counters and locks, and PostgreSQL acting as the source of truth for stored data.

---

## Tech Stack

- Java 17
- Spring Boot 3
- PostgreSQL
- Redis (Spring Data Redis)
- Docker

---

## Approach

### Data Layer

- PostgreSQL stores Users, Bots, Posts, Likes and Comments.
- All writes are performed only after validation against Redis guardrails.
- I have implemented an inherited relationship with JOINED between Users and Bots using the parent abstract class Author.
- There is a unqiue constraint created and stored in the Likes entity which ensures no multiple likes from the sane user to a post.

### Redis Responsibilities

Redis is used for all real-time constraints and concurrency control:

- Virality score tracking
- Bot interaction counters
- Cooldown enforcement
- Notification batching

No in-memory data structures are used in the application.

---

## Guardrails Implementation

### Virality Score

Each interaction updates a Redis key:


post:{id}:virality_score


- Bot reply: +1
- Human like: +20
- Human comment: +50

Implemented using atomic increment operations.

---

### Atomic Locks (Thread Safety)

All concurrency-sensitive logic is handled using Redis atomic commands.

#### Horizontal Cap

- Key: `post:{id}:bot_count`
- Operation: `INCR`

If the value exceeds 100, the request is rejected with HTTP 429.

This guarantees correctness under concurrent requests (no race conditions).

---

#### Vertical Cap

- Constraint: `depth_level <= 20`

Validated before database write.

---

#### Cooldown Cap

- Key: `cooldown:bot_{botId}:human_{userId}`
- Operation: `SET key value NX EX 600`

If the key exists, the interaction is blocked.

This ensures a bot cannot interact with the same user repeatedly within 10 minutes.

---

## Notification System

### Throttling

- Cooldown key: `user:{id}:notif_cooldown`
- Pending list: `user:{id}:pending_notifs`

Logic:

- If cooldown exists → push message to Redis list
- If not → send notification and set cooldown (15 minutes)

---

### Scheduled Aggregation

A scheduled task runs every 5 minutes:

- Reads pending notifications from Redis
- Aggregates them into a summary message
- Clears the Redis list

---

## Running the Project

### 1. Start Infrastructure


docker-compose up -d


This starts:
- PostgreSQL on port 5432
- Redis on port 6379

---

### 2. Run Application


./mvnw spring-boot:run


---

## API Endpoints

- `POST /api/posts` → Create post
- `POST /api/posts/{postId}/comments` → Add comment
- `POST /api/posts/{postId}/like` → Like post

---

## Testing

A Postman collection is included:


postman_collection.json


Steps:

1. Import the collection into Postman
2. Run endpoints in order:
   - Create post
   - Add comment
   - Like post
3. Test edge cases:
   - Multiple bot comments (horizontal cap)
   - Deep comment nesting (vertical cap)
   - Repeated bot-user interaction (cooldown)

---

## Concurrency Handling

The system is designed to handle high concurrency safely:

- Redis `INCR` ensures atomic updates for counters
- `SET NX EX` ensures atomic cooldown locks
- No shared memory is used in the application
- Database writes occur only after Redis validation

This guarantees correctness under concurrent load.

---

## Git History

Commits are structured to reflect incremental development:

- Initial project setup
- Entity and database schema
- Core APIs (posts, comments, likes)
- Redis integration (virality score)
- Atomic lock implementation
- Notification system and scheduler
- Docker setup and final configuration

---

## Author

Kartikey Chauhan
