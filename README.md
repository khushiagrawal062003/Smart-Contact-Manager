# Smart Contact Manager with Advanced Search

A responsive and clean Java Spring Boot web application designed as a portfolio-worthy Contact Relationship Manager (CRM). It features a modern glassmorphic UI, dynamic dashboards, advanced filtering, secure authentication, and bulk CSV operations.

---

## 🔗 Project Links

*   **GitHub Repository**: [Insert GitHub Repository Link Here]
*   **Live Deployment**: [Insert Live Deployment Link Here]

---

## 🚀 Key Features

*   **SaaS Modernization & Dark Mode**: Beautiful glassmorphic UI with vibrant gradients and a responsive theme toggle (Dark/Light mode) persisting state via `localStorage`.
*   **Secure Authentication**: Signup, custom login page, and access control powered by Spring Security 6 and BCrypt password encryption.
*   **Contact CRUD**: Complete details management (Name, Nickname, Email, Phone, Company, Address, Notes, Category, Favorite, and Custom Avatar Picture).
*   **Advanced Search & Filtering**: Case-insensitive search across Name, Mobile, Email, and Company parameters, along with filters for category and starred/favorite status.
*   **Mass CSV Operations**: Bulk import datasets via standard CSV upload, and export the entire directory instantly as a CSV file.
*   **Local Image Serving**: Decoupled file uploads mapping to local system folders, ensuring immediate rendering of contact profile images without server restarts.

---

## 🛠️ Technology Stack

*   **Backend**: Java 17, Spring Boot 3.3.x, Spring Data JPA, Hibernate
*   **Security**: Spring Security 6.x
*   **Database**: MySQL 8.x
*   **Frontend**: Thymeleaf, Bootstrap 5, Bootstrap Icons, Google Fonts (Plus Jakarta Sans)
*   **Build Tool**: Maven

---

## ⚠️ Challenges Faced & Solutions

Building a robust MVC application with modern libraries comes with a few integration hurdles. Here are the key challenges we ran into and how we solved them:

### 1. Thymeleaf 3.1+ `#request` Object Removal
*   **Challenge**: In older Thymeleaf versions, highlighting the active sidebar link was done using `#request.requestURI`. However, Thymeleaf 3.1+ deprecated and disabled `#request` by default for security reasons, causing the template parser to crash with a `500 Internal Server Error`.
*   **Solution**: Instead of relying on request inspection inside the view, we refactored the controllers to pass an `activePage` variable to the Model. The templates now use standard conditional checks (`activePage == 'dashboard'`), which are 100% compatible with Thymeleaf 3.1+.

### 2. Thymeleaf 3.1+ DOM Event Attribute Inlining Block
*   **Challenge**: Event handler attributes like `th:onclick` that use String variable evaluations are disabled by default in Thymeleaf 3.1+ to prevent Cross-Site Scripting (XSS) risks.
*   **Solution**: Migrated inline string parameters to HTML5 standard attributes (`th:data-id` & `th:data-name`) and extracted them in the javascript runtime dynamically via `button.getAttribute()`.

### 3. Zero-State Arithmetic Crash (Division by Zero)
*   **Challenge**: For a newly registered user, the total contacts count is `0`. The dashboard tries to render category statistics using a percentage progress bar. This resulted in a division by zero error (`java.lang.ArithmeticException: / by zero`), crashing the dashboard for all new users.
*   **Solution**: Added a Thymeleaf ternary check (`totalContacts > 0 ? (stat[1] * 100 / totalContacts) : 0`) to safe-guard the width calculation. If a user has 0 contacts, the percentage defaults to `0` cleanly.

### 4. Duplicate UserDetailsService Beans
*   **Challenge**: Declaring the service implementation class with `@Service` and also defining a `UserDetailsService` bean inside the security configuration caused duplicate bean registrations. This led to authentication mismatches where login requests were silently rejected.
*   **Solution**: Cleaned up the security configuration by removing the redundant bean definition and directly injecting the `@Service` bean into the `DaoAuthenticationProvider` setup.

### 5. Custom CSV Parsing Logic
*   **Challenge**: Parsing CSV files with commas inside double-quoted text fields (like addresses or notes) using simple string splits often breaks the database mapping.
*   **Solution**: Wrote a dedicated parser (`CSVHelper.java`) to handle state-based character escaping and field boundary decoding, eliminating the need for bulky third-party parsing libraries.

---

## ⚙️ Local Installation Guide

### Step 1: Create Database
Run the following query in your local MySQL client:
```sql
CREATE DATABASE smart_contact_manager;
```
*(You can seed the initial database tables and a demo user using the script inside [database.sql](file:///C:/Users/Khushi/.gemini/antigravity/scratch/smart-contact-manager/database.sql)).*

### Step 2: Configure Database Credentials
Open `src/main/resources/application.properties` and replace with your local MySQL credentials:
```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

### Step 3: Run
Open the project folder and run the Maven wrapper or command:
```bash
mvn spring-boot:run
```
The application will launch on **http://localhost:8090/**.

*   **Demo User Login**:
    *   **Email**: `demo@contactmanager.com`
    *   **Password**: `password123`
