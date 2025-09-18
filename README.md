# Blue Trading Platform API

A comprehensive Spring Boot REST API for a symbol trading platform with real-time notifications, portfolio management, and market data integration.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL database
- Firebase account (for push notifications)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Blue-back-end
   ```

2. **Configure Database**
   - Update `src/main/resources/application.properties` with your PostgreSQL credentials
   - Or set environment variables:
     ```bash
     export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/blue_db
     export SPRING_DATASOURCE_USERNAME=your_username
     export SPRING_DATASOURCE_PASSWORD=your_password
     ```

3. **Configure Firebase**
   - Place your Firebase service account key in `src/main/resources/`
   - Update Firebase configuration in the application

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The API will be available at: `http://localhost:8080`

## 📋 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 🔐 Authentication Endpoints

### POST /auth/signup
Register a new user account.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Registration successful"
}
```

### POST /auth/signin
Authenticate user and get JWT token.

**Request Body:**
```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "jwt": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful"
}
```

### POST /auth/reset-password
Initiate password reset process.

**Request Body:**
```json
{
  "email": "john.doe@example.com"
}
```

---

## 👤 User Management Endpoints

### GET /users/{userId}
Get user profile information.

**Response:**
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "balance": 10000.00,
  "createdAt": "2024-01-01T00:00:00"
}
```

### PUT /users/{userId}
Update user profile.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Smith",
  "email": "john.smith@example.com"
}
```

### DELETE /users/{userId}
Delete user account.

### POST /users/{userId}/deposit
Deposit funds to user account.

**Request Body:**
```json
{
  "amount": 1000.00
}
```

### GET /users/{userId}/balance-sufficient
Check if user has sufficient balance.

**Query Parameters:**
- `amount` (required): Amount to check

---

## 📈 Trading Endpoints

### POST /trading/orders
Place a new order.

**Query Parameters:**
- `userId` (required): User ID

**Request Body:**
```json
{
  "symbol": "AAPL",
  "quantity": 10,
  "price": 150.00,
  "orderType": "LIMIT",
  "orderSide": "BUY"
}
```

**Response:**
```json
{
  "id": 1,
  "symbol": "AAPL",
  "quantity": 10,
  "price": 150.00,
  "orderType": "LIMIT",
  "orderSide": "BUY",
  "orderStatus": "PENDING",
  "createdAt": "2024-01-01T10:00:00"
}
```

### GET /trading/orders
Get all user orders.

**Query Parameters:**
- `userId` (required): User ID

### GET /trading/orders/open
Get open orders for user.

**Query Parameters:**
- `userId` (required): User ID

### GET /trading/orders/{orderId}
Get specific order details.

**Query Parameters:**
- `userId` (required): User ID

### POST /trading/orders/{orderId}/cancel
Cancel an existing order.

**Query Parameters:**
- `userId` (required): User ID

### GET /trading/order-types
Get available order types.

**Response:**
```json
[
  {"code": "MARKET", "name": "Market Order"},
  {"code": "LIMIT", "name": "Limit Order"},
  {"code": "STOP", "name": "Stop Order"}
]
```

### GET /trading/order-sides
Get available order sides (BUY/SELL).

### GET /trading/order-statuses
Get available order statuses.

---

## 📊 Stock Data Endpoints

### GET /stocks
Get all available stocks.

**Response:**
```json
[
  {
    "id": 1,
    "symbol": "AAPL",
    "name": "Apple Inc.",
    "currentPrice": 150.00,
    "changePercent": 2.5,
    "volume": 1000000,
    "marketCap": 2500000000000
  }
]
```

### GET /stocks/search
Search stocks by query.

**Query Parameters:**
- `query` (required): Search term

### GET /stocks/market-overview
Get market overview with top traded, gainers, and losers.

**Response:**
```json
{
  "topTraded": [...],
  "topGainers": [...],
  "topLosers": [...]
}
```

### GET /stocks/by-industry/{industry}
Get stocks by industry.

### GET /stocks/by-market-cap
Get stocks by market cap range.

**Query Parameters:**
- `min` (optional): Minimum market cap (default: 0)
- `max` (optional): Maximum market cap (default: 1000000000000)

---

## 📋 Watchlist Endpoints

### POST /watchlists
Create a new watchlist.

**Query Parameters:**
- `userId` (required): User ID

**Request Body:**
```json
{
  "name": "Tech Stocks",
  "description": "Technology sector watchlist"
}
```

### GET /watchlists
Get all user watchlists.

**Query Parameters:**
- `userId` (required): User ID

### GET /watchlists/{watchlistId}
Get specific watchlist.

**Query Parameters:**
- `userId` (required): User ID

### PUT /watchlists/{watchlistId}
Update watchlist.

**Query Parameters:**
- `userId` (required): User ID

### DELETE /watchlists/{watchlistId}
Delete watchlist.

**Query Parameters:**
- `userId` (required): User ID

### POST /watchlists/{watchlistId}/stocks/{stockId}
Add symbol to watchlist.

**Query Parameters:**
- `userId` (required): User ID

### DELETE /watchlists/{watchlistId}/stocks/{stockId}
Remove symbol from watchlist.

**Query Parameters:**
- `userId` (required): User ID

### GET /watchlists/{watchlistId}/stocks
Get all stocks in watchlist.

**Query Parameters:**
- `userId` (required): User ID

---

## ⚙️ Settings Endpoints

### GET /settings/user/{userId}
Get user settings.

**Response:**
```json
{
  "id": 1,
  "userId": 1,
  "priceAlertsEnabled": true,
  "orderNotificationsEnabled": true,
  "emailNotificationsEnabled": false,
  "darkModeEnabled": true
}
```

### PUT /settings/user/{userId}
Update user settings.

**Request Body:**
```json
{
  "priceAlertsEnabled": false,
  "orderNotificationsEnabled": true,
  "emailNotificationsEnabled": true
}
```

### PATCH /settings/user/{userId}/{category}
Update specific setting category.

### POST /settings/notifications/price-alerts/toggle
Toggle price alerts.

**Query Parameters:**
- `userId` (required): User ID

### POST /settings/notifications/order-notifications/toggle
Toggle order notifications.

**Query Parameters:**
- `userId` (required): User ID

---

## 📊 Dashboard Endpoints

### GET /dashboard/market-data
Get comprehensive market data for dashboard.

**Response:**
```json
{
  "indices": [...],
  "activeStocks": [...],
  "topGainers": [...],
  "topLosers": [...]
}
```

---

## 💼 Portfolio Endpoints

### GET /user-stocks/portfolio
Get user's symbol portfolio.

**Response:**
```json
[
  {
    "stockId": 1,
    "symbol": "AAPL",
    "quantity": 50,
    "averagePrice": 145.00,
    "currentPrice": 150.00,
    "totalValue": 7500.00,
    "gainLoss": 250.00,
    "gainLossPercent": 3.45
  }
]
```

---

## 🧪 Testing Results

### Application Status
✅ **Application Successfully Started** on `http://localhost:8080`

### Tested Endpoints

#### ✅ Stock Endpoints (Working)
- `GET /stocks` - Get all stocks ✅
- `GET /stocks/search?query=AAPL` - Search stocks ✅
- `GET /stocks/market-overview` - Market overview ✅
- `GET /stocks/{symbol}` - Get symbol by symbol ✅
- `GET /stocks/by-industry/{industry}` - Get stocks by industry ✅
- `GET /stocks/by-market-cap` - Get stocks by market cap ✅

#### ✅ Dashboard Endpoints (Working)
- `GET /dashboard/market-data` - Get market data ✅

#### ⚠️ Authentication Endpoints (Partial Issues)
- `POST /auth/signup` - ⚠️ **Firebase Email Verification Issue**
  - User creation works but email verification fails
  - Error: `USER_NOT_FOUND` in Firebase Auth
  - Issue: App uses local DB but Firebase for email verification
- `POST /auth/signin` - ✅ **Authentication Logic Works**
  - Returns 401 for invalid credentials (expected behavior)
- `POST /auth/forgot-password` - ⚠️ **Same Firebase Issue**
- `POST /auth/change-password` - ⚠️ **Same Firebase Issue**

#### 🔒 Protected Endpoints (Require Authentication)
These endpoints require valid JWT tokens:
- `GET /users/profile` - User profile
- `PUT /users/profile` - Update user
- `DELETE /users/profile` - Delete user
- `POST /users/deposit` - Deposit funds
- `GET /users/balance` - Check balance
- `POST /trading/orders` - Place orders
- `GET /trading/orders` - Get orders
- `DELETE /trading/orders/{id}` - Cancel orders
- `POST /watchlists` - Create watchlist
- `GET /watchlists` - Get watchlists
- `GET /settings` - Get settings
- `PUT /settings` - Update settings
- `GET /user-stocks/portfolio` - Get portfolio
- `POST /user-stocks/buy` - Buy stocks
- `POST /user-stocks/sell` - Sell stocks

### Testing with Postman

#### Prerequisites
1. Start the application: `mvn spring-boot:run`
2. Application runs on `http://localhost:8080`

#### Working Public Endpoints
```bash
# Get All Stocks
GET http://localhost:8080/stocks

# Search Stocks
GET http://localhost:8080/stocks/search?query=AAPL

# Market Overview
GET http://localhost:8080/stocks/market-overview

# Dashboard Market Data
GET http://localhost:8080/dashboard/market-data

# Get Stock by Symbol
GET http://localhost:8080/stocks/AAPL
```

#### Authentication (Note: Email verification has Firebase issues)
```bash
# Sign Up (Creates user but email verification fails)
POST http://localhost:8080/auth/signup
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}

# Sign In (Works for existing users)
POST http://localhost:8080/auth/signin
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

### Known Issues & Recommendations

#### 🚨 Firebase Email Verification Issue
**Problem**: The application uses PostgreSQL for user storage but Firebase Auth for email verification, causing a mismatch.

**Error**: `USER_NOT_FOUND` when trying to generate email verification links.

**Recommendations**:
1. **Option 1**: Create users in Firebase Auth during signup
2. **Option 2**: Use a different email service (SendGrid, SMTP) instead of Firebase
3. **Option 3**: Implement custom email verification without Firebase

#### 🔧 Testing Protected Endpoints
To test protected endpoints, you need to:
1. Create a user manually in the database
2. Sign in to get a JWT token
3. Include the token in the Authorization header: `Bearer <token>`

#### 📊 Database Setup
Ensure PostgreSQL is running and the database schema is properly initialized before testing.

### Environment Setup
Create a Postman environment with the following variables:
- `baseUrl`: `http://localhost:8080`
- `authToken`: (will be set after login)
- `userId`: (will be set after login)

### Authentication Flow
1. **Register a new user** using `POST {{baseUrl}}/auth/signup`
2. **Login** using `POST {{baseUrl}}/auth/signin`
3. **Extract JWT token** from response and set as `authToken` variable
4. **Set Authorization header** for subsequent requests: `Bearer {{authToken}}`

### Sample Test Sequence
1. Register user
2. Login and get token
3. Get user profile
4. Deposit funds
5. Search for stocks
6. Create watchlist
7. Add stocks to watchlist
8. Place an order
9. Check order status
10. Update settings

### Error Handling
The API returns standard HTTP status codes:
- `200`: Success
- `201`: Created
- `400`: Bad Request
- `401`: Unauthorized
- `403`: Forbidden
- `404`: Not Found
- `500`: Internal Server Error

Error responses include descriptive messages:
```json
{
  "error": "User not found",
  "timestamp": "2024-01-01T10:00:00",
  "status": 404
}
```

---

## 🔔 Push Notifications

The API integrates with Firebase Cloud Messaging (FCM) for real-time notifications:

- **Order Updates**: Notifications when orders are placed, filled, or cancelled
- **Price Alerts**: Notifications when watchlist stocks hit target prices
- **Account Updates**: Notifications for profile changes and deposits
- **Settings Changes**: Confirmations for settings modifications

---

## 🛠️ Development

### Project Structure
```
src/main/java/com/techtack/blue/
├── controller/          # REST controllers
├── service/            # Business logic
├── repository/         # Data access layer
├── model/             # Entity models
├── dto/               # Data transfer objects
├── config/            # Configuration classes
└── exception/         # Custom exceptions
```

### Key Technologies
- **Spring Boot 3.x**: Main framework
- **Spring Security**: Authentication & authorization
- **Spring Data JPA**: Database operations
- **PostgreSQL**: Primary database
- **Firebase Admin SDK**: Push notifications
- **JWT**: Token-based authentication
- **Maven**: Dependency management

---

## 📝 Notes

- All monetary values are in USD with 2 decimal precision
- Timestamps are in ISO 8601 format
- Stock prices are updated via Alpha Vantage API integration
- The application includes comprehensive error handling and validation
- Push notifications require valid Firebase device tokens

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

---

## 📄 License

This project is licensed under the MIT License.