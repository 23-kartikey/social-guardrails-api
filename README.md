# Social Guardrails API

A Spring Boot microservice implementing guardrails for user and bot interactions using Redis for atomic operations and PostgreSQL for persistence.

---

## Overview

The system enforces strict constraints on interactions (likes, comments, bot replies) to prevent excessive or unsafe activity. It is designed to be stateless, with Redis handling all real-time counters and locks, and PostgreSQL acting as the source of truth for stored data.

---

## Tech Stack

- Java 17
- Spring Boot 4
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

- I have implemented redis keys and lists to store various aspects of the assignment such as virality score, cooldown timers, pending notification lists, etc.
- Everytime something gets updated I make a key using entity IDs and data to store the information and update/implement them whenever needed.
- I have used TTL on keys in instances such as cooldown timers on bot replies.

post:{id}:virality_score


- Bot reply: +1
- Human like: +20
- Human comment: +50

Implemented using atomic increment operations.
- I have created a method that is specifically designed to increase the virality score of a post based on the post ID and score passed to it. It increased the score by using the post ID as the Redis key as in `post:post_id:virality_score`. This has been implemented in the class Virality Service.

---

### Atomic Locks (Thread Safety)

All concurrency-sensitive logic is handled using Redis atomic commands. These ensure that comment that goes to the database is first checked throught the redis keys which ensures that there is only one thread working before DB persistence.

#### Horizontal Cap

- Key: `post:{id}:bot_count`
- Operation: `INCR`

If the value exceeds 100, the request is rejected with HTTP 429.

This guarantees correctness under concurrent requests (no race conditions).

Implementation:
- I made a method canBotReply() inside BotService class which checks if the concerned has less than 100 bot replies or not. It checks the value depending on the key `post:post_id:bot_count`.
- If the bot limit of 100 replies per post is reached, a custom exception of BotCommentLimitException is thrown which is handled to return an HttpStatus of TOO MANY REQUESTS 429.

---

#### Vertical Cap

- Constraint: `depth_level <= 20`

- Validated before database write. While converting the incoming request to a Comment object in the toComment method inside the CommentService class, the parent_id of the request is extracted to inspect the depth of parent comment using the Redis key `comment:comment_id:depth`. If somehow the value might be deleted due to flush or restart or data inconsistency in Redis, there's a fallback where the depth is extracted from the database it was persisted to. The fallback is slightly slower but prevents unnecessary faiure.
- Moreover if the depth of the parent is 20, the child comment is unallowed to be saved and a custom CommentDepthLimitReachedException is thrown returning a BAD REQUEST 400 Http Status.

---

#### Cooldown Cap

- Key: `cooldown:bot_{botId}:human_{userId}`
- Operation: `SET key value NX EX 600`

If the key exists, the interaction is blocked.

This ensures a bot cannot interact with the same user repeatedly within 10 minutes.
The key is created with a TTL of 10 minutes which ensures that if the bot interacts with the same human again after those concerned 10 minutes, the key doesn't prevent the interaction from happening.
This also throws a custom BotCommentLimitReachedException again returning a 429 BAD REQUEST HttpStatus.

---

## Notification System

### Throttling

- Cooldown key: `user:{id}:notif_cooldown`
- Pending list: `user:{id}:pending_notifs`

Logic:

- If cooldown exists → push message to Redis list
- If not → send notification and set cooldown (15 minutes)

#### Implementation

User notification:

- Whenever a user interacts with a post a notification is sent to the post author and simultaneously a Redis key `user:post_author_id:notif_cooldown` is set with value "1" and a TTL of 10 minutes.
- A user interaction never checks for the notif_cooldown key's existence while sending notification because a user interaction notification isn't counted as spam.

Bot notification:

- A bot reply is followed by a call to the handleNotification method inside the NotificationService class where the cooldown timer on the user is checked. If there is no timer on the user. The notification of the bot interaction is sent to the user.
- If there is a cooldown key in place. The notification message is right pushed to the Redis list `user:user_id:pending_notifs`.

---

### Scheduled Aggregation

A scheduled task runs every 5 minutes:

- Reads pending notifications from Redis
- Aggregates them into a summary message
- Clears the Redis list

#### Implementation

- Everytime the 5 minute scheduler runs, all the keys stored with the Redis key `user:user_id:pending_notifs` are extracted by using the * wildcard.
- These keys are then traversed one by one, counting all the messages inside them and extracting the earliest notification user to send the summary notificaiton telling the user that Bot X and N other interacted with your posts.
- All of the messages inside the keys are popped after this, leading to a clean slate for all the users.

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

### Bot and User creation endpoints

- `POST /api/bots` → Create Bot
- `POST /api/users` → Create User
  
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

> 💡 Test the endpoints and limit on comments by making use of the CLI and sending a 100 or even 200 requests at once. This proves concurrency control and rate limiting implemented on the bot comments.

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
> It has been fun making this repository. The things I had to learn and implement were new to me, and opened the possibility of new ideas and features I can implement in my future work.
> I thank you for reading this long.
> Bye!
> Best regards,
> Kartikey
