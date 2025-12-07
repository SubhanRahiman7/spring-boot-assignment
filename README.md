# RideShare Backend API

A robust, secure RESTful API for a ride-sharing application built with Spring Boot. This application provides user authentication, role-based access control, and complete ride management functionality for both passengers and drivers.

## 🚀 Features

- **User Authentication & Authorization**
  - JWT-based authentication
  - User registration and login
  - Role-based access control (USER and DRIVER roles)
  - Secure password encryption using BCrypt

- **Ride Management**
  - Create ride requests (Passengers)
  - View pending ride requests (Drivers)
  - Accept ride requests (Drivers)
  - Complete rides (Users and Drivers)
  - View ride history (Users)

- **Security**
  - JWT token-based authentication
  - Spring Security integration
  - Stateless session management
  - Protected API endpoints

- **Data Validation**
  - Request validation using Jakarta Bean Validation
  - Comprehensive error handling
  - Custom exception handling

## 🛠️ Tech Stack

- **Framework**: Spring Boot 4.0.0
- **Language**: Java 17
- **Database**: MongoDB
- **Security**: Spring Security + JWT (JJWT 0.11.5)
- **Build Tool**: Maven
- **Libraries**:
  - Lombok (for reducing boilerplate code)
  - Jackson (for JSON processing)
  - Jakarta Bean Validation

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 17** or higher
- **Maven 3.6+**
- **MongoDB** (running locally or accessible instance)
- **Git** (optional, for version control)

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd SpringBoot-Project
```

### 2. Configure MongoDB

Ensure MongoDB is running on your local machine:

```bash
# Start MongoDB (if installed locally)
mongod
```

Or use a remote MongoDB instance by updating the connection string in `application.properties`.

### 3. Configure Application Properties

Update `src/main/resources/application.properties` with your configuration:

```properties
spring.application.name=rideshare-backend
server.port=8081

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/rideshare
spring.data.mongodb.database=rideshare

# JWT Configuration
jwt.secret=YourVerySecureJWTSecretKeyMustBeAtLeast32BytesLong!
jwt.expiration=86400000
```

**Important**: Change the `jwt.secret` to a secure, random string of at least 32 characters in production.

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/SpringBoot_project-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8081`

## 📚 API Documentation

### Base URL
```
http://localhost:8081
```

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123",
  "role": "ROLE_USER"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "role": "ROLE_USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "role": "ROLE_USER"
}
```

### Ride Endpoints

#### Create Ride Request (USER only)
```http
POST /api/v1/rides
Authorization: Bearer <token>
Content-Type: application/json

{
  "pickupLocation": "123 Main St, City",
  "dropLocation": "456 Oak Ave, City"
}
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "userId": "john_doe",
  "driverId": null,
  "pickupLocation": "123 Main St, City",
  "dropLocation": "456 Oak Ave, City",
  "status": "REQUESTED",
  "createdAt": "2024-01-15T10:30:00.000Z"
}
```

#### Get User Rides (USER only)
```http
GET /api/v1/user/rides
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "userId": "john_doe",
    "driverId": "driver123",
    "pickupLocation": "123 Main St, City",
    "dropLocation": "456 Oak Ave, City",
    "status": "ACCEPTED",
    "createdAt": "2024-01-15T10:30:00.000Z"
  }
]
```

#### Get Pending Ride Requests (DRIVER only)
```http
GET /api/v1/driver/rides/requests
Authorization: Bearer <token>
```

**Response:**
```json
[
  {
    "id": "507f1f77bcf86cd799439011",
    "userId": "john_doe",
    "driverId": null,
    "pickupLocation": "123 Main St, City",
    "dropLocation": "456 Oak Ave, City",
    "status": "REQUESTED",
    "createdAt": "2024-01-15T10:30:00.000Z"
  }
]
```

#### Accept Ride (DRIVER only)
```http
POST /api/v1/driver/rides/{rideId}/accept
Authorization: Bearer <token>
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "userId": "john_doe",
  "driverId": "driver123",
  "pickupLocation": "123 Main St, City",
  "dropLocation": "456 Oak Ave, City",
  "status": "ACCEPTED",
  "createdAt": "2024-01-15T10:30:00.000Z"
}
```

#### Complete Ride (USER or DRIVER)
```http
POST /api/v1/rides/{rideId}/complete
Authorization: Bearer <token>
```

**Response:**
```json
{
  "id": "507f1f77bcf86cd799439011",
  "userId": "john_doe",
  "driverId": "driver123",
  "pickupLocation": "123 Main St, City",
  "dropLocation": "456 Oak Ave, City",
  "status": "COMPLETED",
  "createdAt": "2024-01-15T10:30:00.000Z"
}
```

### Error Responses

All error responses follow this format:

```json
{
  "error": "ERROR_CODE",
  "message": "Error description",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

**Common Error Codes:**
- `VALIDATION_ERROR` - Request validation failed
- `BAD_REQUEST` - Invalid request parameters
- `NOT_FOUND` - Resource not found
- `AUTHENTICATION_ERROR` - Authentication failed
- `INTERNAL_ERROR` - Server error

## 🔐 Security

- **JWT Authentication**: All protected endpoints require a valid JWT token in the `Authorization` header
- **Password Encryption**: Passwords are encrypted using BCrypt before storage
- **Role-Based Access**: Endpoints are protected based on user roles (USER/DRIVER)
- **Stateless Sessions**: No server-side session storage

### Using the API

1. **Register or Login** to get a JWT token
2. **Include the token** in all subsequent requests:
   ```
   Authorization: Bearer <your-jwt-token>
   ```

## 📁 Project Structure

```
SpringBoot-Project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/rideshare/
│   │   │       ├── config/
│   │   │       │   └── SecurityConfig.java          # Spring Security configuration
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java          # Authentication endpoints
│   │   │       │   ├── RideController.java          # Ride management endpoints
│   │   │       │   ├── DriverController.java        # Driver-specific endpoints
│   │   │       │   └── UserController.java         # User-specific endpoints
│   │   │       ├── dto/
│   │   │       │   ├── AuthResponse.java           # Authentication response DTO
│   │   │       │   ├── LoginRequest.java            # Login request DTO
│   │   │       │   ├── RegisterRequest.java        # Registration request DTO
│   │   │       │   ├── CreateRideRequest.java      # Create ride request DTO
│   │   │       │   ├── RideResponse.java            # Ride response DTO
│   │   │       │   └── ErrorResponse.java           # Error response DTO
│   │   │       ├── exception/
│   │   │       │   ├── BadRequestException.java     # Bad request exception
│   │   │       │   ├── NotFoundException.java       # Not found exception
│   │   │       │   └── GlobalExceptionHandler.java  # Global exception handler
│   │   │       ├── model/
│   │   │       │   ├── User.java                    # User entity
│   │   │       │   └── Ride.java                    # Ride entity
│   │   │       ├── repository/
│   │   │       │   ├── UserRepository.java          # User repository
│   │   │       │   └── RideRepository.java          # Ride repository
│   │   │       ├── service/
│   │   │       │   ├── AuthService.java             # Authentication service
│   │   │       │   └── RideService.java             # Ride management service
│   │   │       ├── util/
│   │   │       │   ├── JwtUtil.java                 # JWT utility class
│   │   │       │   └── JwtAuthenticationFilter.java # JWT authentication filter
│   │   │       └── RideShareApplication.java        # Main application class
│   │   └── resources/
│   │       └── application.properties               # Application configuration
│   └── test/
│       └── java/
│           └── org/example/springboot_project/
│               └── SpringBootProjectApplicationTests.java
├── pom.xml                                          # Maven dependencies
└── README.md                                        # This file
```

## 🧪 Testing

Run tests using Maven:

```bash
mvn test
```

## 🚦 Ride Status Flow

1. **REQUESTED** - Initial status when a ride is created by a user
2. **ACCEPTED** - Status after a driver accepts the ride
3. **COMPLETED** - Final status when the ride is completed

## 📝 Example Usage Flow

1. **Register a User**:
   ```bash
   curl -X POST http://localhost:8081/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"john","password":"pass123","role":"ROLE_USER"}'
   ```

2. **Register a Driver**:
   ```bash
   curl -X POST http://localhost:8081/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"driver1","password":"pass123","role":"ROLE_DRIVER"}'
   ```

3. **Login as User**:
   ```bash
   curl -X POST http://localhost:8081/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"john","password":"pass123"}'
   ```

4. **Create a Ride** (use token from login):
   ```bash
   curl -X POST http://localhost:8081/api/v1/rides \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer <token>" \
     -d '{"pickupLocation":"Location A","dropLocation":"Location B"}'
   ```

5. **Login as Driver and Accept Ride**:
   ```bash
   curl -X POST http://localhost:8081/api/v1/driver/rides/{rideId}/accept \
     -H "Authorization: Bearer <driver-token>"
   ```

## 🔒 Security Best Practices

- Always use HTTPS in production
- Store JWT secret securely (use environment variables)
- Implement rate limiting for authentication endpoints
- Regularly rotate JWT secrets
- Use strong password policies
- Implement token refresh mechanism for production

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- MongoDB for the database solution
- JJWT library for JWT implementation

---

**Note**: This is a backend API. For a complete ride-sharing application, you would need a frontend application to consume these APIs.
