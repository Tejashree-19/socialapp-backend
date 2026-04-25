# 🚀 SocialApp Backend

## 📌 Overview
This project is a Spring Boot backend for a social media platform. It supports posts, comments, likes, and includes bot detection with strict concurrency control using Redis.

---

## 🧠 Architecture

- **Spring Boot** → REST API
- **PostgreSQL** → persistent storage
- **Redis** → real-time control (cooldowns, counters, limits)
- **Docker** → local environment setup

---

## ⚡ Features

### 📝 Posts & Comments
- Users can create posts and comments
- Nested comments supported with depth validation

---

### 🤖 Bot Detection
- Users with `ID > 1000` are treated as bots
- Bots are restricted with:
  - Cooldowns
  - Maximum reply limits

---

### 🔒 Concurrency Control

To handle race conditions under heavy load:

- Redis is used as a **gatekeeper**
- Atomic operations ensure:
  - Increment bot reply count
  - Check limit
  - Allow or reject request

👉 Guarantees:
- No race conditions
- Strict limit of **100 bot replies per post**
- Works correctly under **high concurrency (200+ requests)**

---

### ⏱ Rate Limiting

- Each bot has a cooldown (10 minutes per post)
- Implemented using Redis TTL

---

### 🔔 Notification System

- First interaction → instant notification
- Further interactions → stored in Redis queue
- Prevents notification spam

---

## 🧪 Tech Stack

- Java (Spring Boot)
- PostgreSQL
- Redis
- Docker

---

## 🐳 Running the Project

### Start services
bash
docker-compose up -d

## 🔥 Future Improvements

- Add authentication (JWT-based login)
- Add pagination for posts/comments
- Add real-time updates using WebSockets
