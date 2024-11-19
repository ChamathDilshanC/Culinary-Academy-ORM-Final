# 🎓 Culinary Academy Management System

A modern student registration and course management system built with Hibernate ORM for The Culinary Academy of Sri Lanka.

## 🚀 Features

- **Student Management**
  - Complete CRUD operations for student records
  - Secure password encryption using BCrypt
  - Input validation for emails, phone numbers, and other fields
  - Multiple course registration support

- **Course Management**
  - Full CRUD operations for culinary programs
  - Automated fee calculation
  - Duration tracking
  - Course capacity management

- **User Authentication & Authorization**
  - Role-based access control (Admin, Admissions Coordinator)
  - Secure password encryption
  - Session management
  - Custom exception handling for registration/login

- **Advanced Database Features**
  - Hibernate ORM integration
  - Cascade operations support
  - Lazy and eager loading implementations
  - Custom HQL queries
  - First-level and second-level caching

## 🛠️ Technical Stack

- Java
- Hibernate ORM
- MySQL
- BCrypt Password Encryption
- Regular Expressions for Validation
- Layered Architecture (MVC Pattern)

## 📋 Prerequisites

- Java JDK 11 or higher
- Maven
- MySQL Server
- IDE (IntelliJ IDEA recommended)

## 🔧 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/ChamathDilshanC/Culinary-Academy-ORM-Final.git
   cd Culinary-Academy-ORM-Final
   ```

2. **Configure Database**
   - Create a MySQL database
   - Update `hibernate.cfg.xml` or `application.properties` with your database credentials

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn exec:java -Dexec.mainClass="com.culinary.Main"
   ```

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/
│   │   ├── com/culinary/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dao/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── service/
│   │   │   ├── util/
│   │   │   └── Main.java
│   ├── resources/
│   │   └── hibernate.cfg.xml
└── test/
    └── java/
```

## 💡 Key Features Implementation

### Student Registration Process
1. Initial interview by admissions coordinator
2. Document verification
3. Course selection
4. Fee payment processing
5. Registration confirmation

### Available Courses
| Program ID | Program Name | Duration | Fee (LKR) |
|------------|--------------|-----------|------------|
| CA1001 | Professional Cooking | 1 year | 120,000.00 |
| CA1003 | Baking & Pastry Arts | 6 months | 60,000.00 |
| CA1004 | International Cuisine | 1 year | 100,000.00 |
| CA1005 | Culinary Management | 1 year | 150,000.00 |
| CA1006 | Food Safety and Hygiene | 3 months | 40,000.00 |

## 🔐 Security Features

- Password encryption using BCrypt
- Role-based access control
- Input validation using RegEx
- Custom exception handling
- Secure session management

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is part of the Graduate Diploma in Software Engineering coursework (ITS1155- ORM Concepts).

## 👥 Authors

- Chamath Dilshan C

## 🙏 Acknowledgments

- IJSE Sri Lanka
- Course instructors and mentors
- Fellow students who provided feedback and suggestions
