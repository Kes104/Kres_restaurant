# Kres Restaurant — Microservice-based Backend

A production-style restaurant management system built with **Micronaut**, **PostgreSQL**, **RabbitMQ**, and a **Telegram Bot** interface. Designed to solve the core problem of minimizing customer wait times and synchronizing kitchen operations in a busy restaurant.

---

## Project Overview

Customers interact with the restaurant entirely through a Telegram bot — from browsing the menu and placing orders to receiving real-time kitchen updates and requesting their bill. The backend is a modular monolith structured around clean domain boundaries, with event-driven communication via RabbitMQ between the order and kitchen layers.

---

## Architecture

```
Customer (Telegram Bot)
        |
        | Webhook
        ▼
API Gateway (WebhookController)
        |
        ├── SessionService    → Customer identity + dining session lifecycle
        ├── TableService      → Table allocation and status management
        ├── MenuService       → Dish catalog and category browsing
        ├── OrderService      → Order creation and item tracking
        |        |
        |        | Publishes events
        |        ▼
        |    RabbitMQ (order-exchange)
        |        |
        |        ├── chef1-queue     → Chef Ravi (Breakfast)
        |        ├── chef2-queue     → Chef Lakshmi (Lunch)
        |        ├── chef3-queue     → Chef Arjun (Snacks, 4:30PM–9:30PM)
        |        ├── billing-queue   → BillingService
        |        └── notification-queue → NotificationService
        |
        ├── BillingService    → Bill generation with 5% GST
        └── NotificationService → Customer updates via Telegram
```

---

## Domain Structure

```
src/main/java/com/kar/
  table/        → RestaurantTable entity, repository, service, controller
  menu/         → Dish, MenuCategory entities, repository, service, controller
  session/      → Customer, DiningSession entities, repository, service, controller
  order/        → Order, OrderItem, OrderEvent entities, repository, service, controller
  chef/         → Chef, ChefOrder, ChefOrderItem entities, repository, service, controller
  billing/      → Bill entity, repository, service, consumer, controller
  notification/ → NotificationQueueConsumer
  gateway/      → WebhookController, TelegramService, TelegramConfig
```

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Micronaut 4.6 | Backend framework (compile-time DI, zero reflection) |
| Java 21 | Language (virtual threads for async processing) |
| PostgreSQL 16 | Primary database |
| RabbitMQ 3.13 | Event-driven messaging between order and kitchen |
| Docker Compose | Local infrastructure |
| Telegram Bot API | Customer-facing interface |
| Gradle | Build tool |

---

## Key Features

**Customer flow via Telegram:**
- `/start` — register and create a dining session
- `/table` — allocate a table based on party size
- `/menu` — browse dishes by category with prices
- `order <dish> <qty>` — place an order with estimated wait time
- `/bill` — generate itemized bill with 5% GST and release table
- `/clear` — reset session

**Chef flow via Telegram:**
- `/chef` — enter chef ID to view pending orders
- `complete <orderId>` — mark order as completed, triggers customer notification

**Owner flow via Telegram:**
- `/owner` — generates a one-time OTP in server logs
- `owner <otp>` — access owner dashboard showing occupied tables, total income, and active chefs

**Backend features:**
- Order routing to correct chef queue based on dish meal category
- Chef availability check based on working hours (Chef 3 only 4:30PM–9:30PM)
- Estimated wait time per order (chef's current remaining time + dish prep time)
- Priority escalation — sessions waiting over 15 minutes are bumped to HIGH priority
- Meal synchronizer — holds customer notification until all chef orders for a session are complete
- Automatic RabbitMQ queue and exchange declaration on startup

---

## Prerequisites

- Java 21
- Docker and Docker Compose
- Node.js (for localtunnel during local testing)
- Telegram Bot token (create via @BotFather)

---

## Local Setup

**1. Clone the repository:**
```bash
git clone https://github.com/Kes104/restaurant.git
cd restaurant
```

**2. Start infrastructure:**
```bash
docker compose up -d
```

**3. Configure `src/main/resources/application.properties`:**
```properties
micronaut.application.name=restaurant
micronaut.server.port=8040

datasources.default.url=jdbc:postgresql://localhost:5433/restaurant
datasources.default.username=restaurant_user
datasources.default.password=restaurant_pass
datasources.default.driver-class-name=org.postgresql.Driver
datasources.default.db-type=postgres
datasources.default.dialect=POSTGRES
datasources.default.schema-generate=NONE

rabbitmq.uri=amqp://restaurant_user:restaurant_pass@localhost:5672

telegram.bot.token=YOUR_TELEGRAM_BOT_TOKEN
telegram.bot.username=your_bot_username
```

**4. Create database tables:**
```bash
docker exec -it restaurant_postgres psql -U restaurant_user -d restaurant -c "
CREATE TABLE IF NOT EXISTS chef (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), mealtype VARCHAR(50), active_from TIMESTAMP, active_till TIMESTAMP, chef_status VARCHAR(50) DEFAULT 'FREE', current_order_remaining INTEGER DEFAULT 0);
CREATE TABLE IF NOT EXISTS restaurant_table (id BIGSERIAL PRIMARY KEY, size INTEGER, status VARCHAR(50));
CREATE TABLE IF NOT EXISTS customer (id BIGSERIAL PRIMARY KEY, telegram_chat_id BIGINT, name VARCHAR(255));
CREATE TABLE IF NOT EXISTS dining_session (id BIGSERIAL PRIMARY KEY, customer_id BIGINT, table_id BIGINT, status VARCHAR(50), start_time TIMESTAMP, end_time TIMESTAMP);
CREATE TABLE IF NOT EXISTS menu_category (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), chef_id BIGINT);
CREATE TABLE IF NOT EXISTS dish (id BIGSERIAL PRIMARY KEY, name VARCHAR(255), price NUMERIC(10,2), category_id BIGINT, available BOOLEAN, description VARCHAR(500), prep_time INTEGER);
CREATE TABLE IF NOT EXISTS orders (id BIGSERIAL PRIMARY KEY, session_id BIGINT, created_at TIMESTAMP, status VARCHAR(50));
CREATE TABLE IF NOT EXISTS order_item (id BIGSERIAL PRIMARY KEY, order_id BIGINT, dish_id BIGINT, quantity INTEGER);
CREATE TABLE IF NOT EXISTS chef_order (id BIGSERIAL PRIMARY KEY, order_id BIGINT, chef_id BIGINT, status VARCHAR(50), priority VARCHAR(50), created_at TIMESTAMP);
CREATE TABLE IF NOT EXISTS chef_order_item (id BIGSERIAL PRIMARY KEY, chef_order_id BIGINT, dish_id BIGINT, quantity INTEGER);
CREATE TABLE IF NOT EXISTS bill (id BIGSERIAL PRIMARY KEY, session_id BIGINT, total_amount NUMERIC(10,2), tax_amount NUMERIC(10,2), final_amount NUMERIC(10,2), status VARCHAR(50), created_at TIMESTAMP);
"
```

**5. Seed initial data:**
```bash
docker exec -it restaurant_postgres psql -U restaurant_user -d restaurant -c "
INSERT INTO chef (name, mealtype, active_from, active_till) VALUES
('Chef Ravi', 'BREAKFAST', '2026-01-01 09:00:00', '2026-01-01 22:00:00'),
('Chef Lakshmi', 'LUNCH', '2026-01-01 11:30:00', '2026-01-01 22:00:00'),
('Chef Arjun', 'SNACKS', '2026-01-01 16:30:00', '2026-01-01 21:30:00');

INSERT INTO menu_category (name, chef_id) VALUES ('Breakfast', 1), ('Lunch', 2), ('Snacks', 3);

INSERT INTO restaurant_table (size, status) VALUES
(2,'AVAILABLE'),(2,'AVAILABLE'),(2,'AVAILABLE'),(2,'AVAILABLE'),(2,'AVAILABLE'),(2,'AVAILABLE'),
(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),
(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),(4,'AVAILABLE'),
(8,'AVAILABLE'),(8,'AVAILABLE');

INSERT INTO dish (name, price, category_id, available, prep_time) VALUES
('Idli', 20.00, 1, true, 10), ('Masala Dosa', 40.00, 1, true, 15),
('Vada', 25.00, 1, true, 8), ('Upma', 30.00, 1, true, 12), ('Poha', 25.00, 1, true, 10),
('Sambar Rice', 60.00, 2, true, 20), ('Dal Rice', 55.00, 2, true, 15),
('Veg Thali', 120.00, 2, true, 25), ('Chapati', 30.00, 2, true, 10),
('Paneer Butter Masala', 110.00, 2, true, 20), ('Veg Biryani', 100.00, 2, true, 25),
('Masala Chai', 15.00, 3, true, 3), ('Filter Coffee', 20.00, 3, true, 3),
('Samosa', 30.00, 3, true, 8), ('Bread Pakoda', 35.00, 3, true, 10),
('Veg Sandwich', 45.00, 3, true, 12), ('Pav Bhaji', 60.00, 3, true, 15);
"
```

**6. Run the application:**
```bash
./gradlew run
```

**7. Start public tunnel (for Telegram webhook):**
```bash
npx localtunnel --port 8040
```

**8. Register Telegram webhook:**
```bash
curl -X POST "https://api.telegram.org/bot<YOUR_TOKEN>/setWebhook" \
  -H "Content-Type: application/json" \
  -d '{"url": "https://YOUR_TUNNEL_URL/webhook/telegram"}'
```

---

## Running Tests

```bash
./gradlew test
```

Test report available at `build/reports/tests/test/index.html`

---

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | `/menu/dishes` | All dishes |
| GET | `/menu/dishes/{id}` | Dish by ID |
| GET | `/menu/categories` | All categories |
| GET | `/menu/dishes/category/{categoryId}` | Dishes by category |
| GET | `/tables` | All tables |
| GET | `/tables/available` | Available tables |
| GET | `/tables/size/{size}` | Tables by size |
| PUT | `/tables/{id}/status` | Update table status |
| POST | `/sessions/start` | Find or create session |
| GET | `/sessions/customer/{customerId}` | Sessions by customer |
| POST | `/orders` | Create order |
| GET | `/orders/session/{sessionId}` | Orders by session |
| POST | `/orders/{orderId}/items` | Add item to order |
| GET | `/orders/{orderId}/items` | Items in order |
| POST | `/billing/generate/{sessionId}` | Generate bill |
| GET | `/billing/session/{sessionId}` | Get bills by session |
| POST | `/webhook/telegram` | Telegram webhook endpoint |

---

## Project Structure

```
restaurant/
  src/
    main/
      java/com/kar/     → Application source code
      resources/
        application.properties → Configuration
    test/
      java/com/kar/     → Test files
  docker-compose.yml    → PostgreSQL + RabbitMQ setup
  build.gradle          → Dependencies and build config
  README.md             → This file
```

---

## Author

Keshav — Computer Science Engineering, Dayananda Sagar College of Engineering, Bangalore
GitHub: [Kes104](https://github.com/Kes104)