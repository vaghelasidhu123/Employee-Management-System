# Employee Management System (EMS)

A complete Employee Management System built with **Spring Boot** and **MySQL** that helps organizations manage employee data, user accounts, and HR operations through a secure web interface.

## 📸 Application Screenshots

### 1. Login Page
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/92a5a400-fca3-426a-a53b-8bc42bb4870e" />
Secure login interface with username/password authentication and links for registration and password recovery.

### 2. Registration Page
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/d88029cd-ec54-43f5-888a-b2da52f14ad9" />
User registration form with role selection, password strength validation, and terms agreement.

### 3. Dashboard
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/9e3cbb2a-69ac-4db3-9474-377e1a3f3338" />
Main dashboard showing real-time statistics, quick actions, recent activities, and system information.

### 4. User Profile
![Profile](Screenshot%20(414).png)
Complete user profile displaying account information, security details, and account status.

### 5. Change Password
![Change Password](Screenshot%20(415).png)
Password change interface with current password verification and new password confirmation.

### 6. Edit Profile
![Edit Profile](Screenshot%20(416).png)
Profile editing form with validation and real-time updates.

### 7. Employee Directory
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/ac46bb0b-42c3-4f61-b3c5-b15279d35646" />
Employee listing with search, filter, pagination, and action buttons for each employee.

### 8. Update Employee
<img width="1920" height="1080" alt="image" src="https://github.com/user-attachments/assets/3c00e538-6af5-4f42-bcc7-94c365b55531" />
Employee information editing form with active/inactive status toggle.

### 9. Employee Details
![Employee Details](Screenshot%20(419).png)
Detailed employee profile view showing personal, employment, and status information.

## 🚀 Quick Setup

### Prerequisites:
- Java JDK 21
- Apache Maven
- MySQL 8.0+

### Steps:
1. **Create MySQL Database:**
```sql
CREATE DATABASE ems_db;
CREATE USER 'ems_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON ems_db.* TO 'ems_user'@'localhost';
FLUSH PRIVILEGES;
```

2. **Configure `application.properties`:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ems_db
spring.datasource.username=ems_user
spring.datasource.password=password123
spring.jpa.hibernate.ddl-auto=update
server.port=8080
```

3. **Run the Application:**
```bash
mvn clean compile
mvn spring-boot:run
```

4. **Access at:** http://localhost:8080

## 🔐 Default Login
- **Admin:** `admin` / `admin123`
- **Users:** Register via signup page

## 📁 Main Components

### Entities:
- **User** - System users with roles (ADMIN, MANAGER, USER)
- **Employee** - Employee records with personal and employment details
- **PasswordResetToken** - Password recovery tokens

### Controllers:
- **AuthController** - Handles login, registration, password management
- **EmployeeController** - Manages employee CRUD operations

### Services:
- **UserService** - User registration and management
- **EmployeeService** - Employee business logic
- **PasswordResetService** - Password recovery functionality

## ✅ Features

### User Management:
- ✅ Role-based access control (Admin/Manager/User)
- ✅ Secure authentication with Spring Security
- ✅ Profile management and password change
- ✅ Password reset via email tokens

### Employee Management:
- ✅ Complete CRUD operations for employees
- ✅ Search, filter, and pagination
- ✅ Department categorization
- ✅ Salary and position tracking

### Dashboard & Reports:
- ✅ Real-time statistics and counts
- ✅ Quick action buttons
- ✅ Recent activity log
- ✅ Responsive UI with Bootstrap

## 🛠️ Built With

- **Java 21** - Programming language
- **Spring Boot** - Application framework
- **MySQL** - Database management
- **Maven** - Build automation
- **Thymeleaf** - Template engine
- **Bootstrap** - Frontend framework

## 📞 Contact

For support or questions, please refer to the code documentation or create an issue in the repository.

---
*Project developed for educational and practical HR management purposes.*
