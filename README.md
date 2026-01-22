# 📦 Shop Management System

A professional Desktop CRUD application built with **Java Swing** and **Hibernate 6**. This project demonstrates role-based access control, relational database management, and professional UI state handling.

---

## 🚀 Key Features
- **Role-Based Login:** Secured access for `ADMIN` and `USER` roles.
- **Automated ORM:** Uses Hibernate to map Java Objects to MySQL tables.
- **One-to-One Relational Mapping:** Links Products to Product Images using `CascadeType.ALL`.
- **Search Functionality:** Real-time data filtering using **HQL** (Hibernate Query Language).
- **Secure Configuration:** Private credentials (passwords) are handled via a local `.properties` file to keep them off GitHub.

---

## 🛠️ Technology Stack
- **Language:** Java 17+
- **Database:** MySQL 8.x
- **Framework:** Hibernate 6 (ORM)
- **GUI:** Java Swing (AWT)
- **Build Tool:** Maven

---


## 🏗️ Project Architecture & Data Flow

The application follows a Modular Layered Architecture , ensuring a clean separation between the User Interface and the Database logic.

1. View Layer (com.view): Handles the UI components and user events.
2. Model Layer (com.model): Contains JPA-annotated entities representing the database schema.
3. Utility Layer (com.util): Manages the Hibernate SessionFactory and database connectivity.
4. Data Layer (MySQL): Persistent storage for Users, Products, and Images.

---

## ※ Core Features

🔐 Security & Role-Based LoginCore Features
- Logic: The system distinguishes between ADMIN and USER roles.
- Access Control: Only Admins can access the User Management module. Regular users are redirected or restricted based on their credentials.
- Validation: Incorrect credentials trigger an automatic redirect to the Main Dashboard to reset the user flow.

📦 Product & Image Management (One-to-One)
- Logic: Implements a bidirectional @OneToOne mapping between Product and ProductImage.
- Cascading: Using CascadeType.ALL, the system ensures that saving or deleting a product automatically manages the associated image record in the database.

⚡ Foolproof UI & UX
- Button State Management: Buttons toggle based on context (e.g., "Add" is disabled when a row is selected for "Update").
- Visual Styling: Color-coded buttons (Green for Add, Red for Delete, Blue for Update) provide immediate visual cues.
- Real-time Search: A search bar integrated with HQL LIKE queries for instant data filtering.

---

# 💡 Key Logic Explanation
HQL (Hibernate Query Language)
- The project uses HQL instead of native SQL to remain database-independent.
- Example Query: from Product p where p.name LIKE :q
- Benefit: This allows the code to work with any SQL database (MySQL, PostgreSQL, Oracle) without changing the Java logic.

Memory Management
- Logic: Every frame transition uses the .dispose() method.
- Benefit: This prevents "window stacking" and ensures that the application uses minimal RAM by clearing unused GUI resources.

---


## 💾 Database Schema (SQL)
Run the following script in your MySQL Workbench to set up the required environment:

```sql
CREATE DATABASE IF NOT EXISTS hibernateswingproject;
USE hibernateswingproject;

-- User Table
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

-- Product Table
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    price DOUBLE,
    description TEXT
);

-- Product Image Table (One-to-One)
CREATE TABLE product_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    image_url VARCHAR(255),
    product_id INT UNIQUE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Default Admin Account
INSERT INTO users (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');
```

---
## ✅ To run this project, you must create a file that Git will ignore to protect your database password.
- Navigate to src/main/resources/
- Create a file named db.properties and add the following :
- db.username=root
- db.password=YOUR_PASSWORD_HERE
