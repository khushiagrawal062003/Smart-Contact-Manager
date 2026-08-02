# 📁 Smart Contact Manager

This repository contains the **Smart Contact Manager** full-stack web application used for securely managing professional contact directories, custom grouping categories, bulk CSV imports, and theme preferences.

---

## 🔗 Project Links

*   **GitHub Repository**: [https://github.com/khushiagrawal062003/Smart-Contact-Manager](https://github.com/khushiagrawal062003/Smart-Contact-Manager)
*   **Live Deployment**: [https://smart-contact-manager-bkxe.onrender.com](https://smart-contact-manager-bkxe.onrender.com)

---

## 📂 Project Structure

*   `src/main/java/` - Java Spring Boot backend server following MVC pattern (Controllers, Services, Repositories, Entities).
*   `src/main/resources/templates/` - Server-side rendered frontend UI using Thymeleaf templates.
*   `src/main/resources/static/` - Client-side static resources (Glassmorphic CSS styles and JS event handlers).

---

## ⚙️ Setup Instructions

### Backend & Database Setup

1.  **Create Database**: Connect to your local PostgreSQL instance and create the database:
    ```sql
    CREATE DATABASE smart_contact_manager;
    ```
2.  **Configure Environment**: Open `src/main/resources/application.properties` and update your PostgreSQL credentials:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/smart_contact_manager
    spring.datasource.username=your_postgres_username
    spring.datasource.password=your_postgres_password
    spring.jpa.hibernate.ddl-auto=update
    ```
3.  **Run the Server**: Open the project folder and run the Maven wrapper command:
    ```bash
    mvn spring-boot:run
    ```
    The application API and web pages will be available locally at: **http://localhost:8090**.

---

## 🚀 Building for Production

*   Run the Maven package command to compile and build the production-ready fat JAR file:
    ```bash
    mvn clean package -DskipTests
    ```
    The built executable JAR file will be saved inside the `target/` directory.

---

## 🌐 Deployment

The application is deployed on **Render** using a multi-stage `Dockerfile` setup:
*   **Stage 1 (Build)**: Compiles the source code with Maven dependencies.
*   **Stage 2 (Runtime)**: Copies the built runner JAR into a lightweight eclipse-temurin JRE image (~150MB footprint).
*   **Database**: Linked to a cloud hosted Render PostgreSQL instance.

---

## 🤝 Contributing

1.  Fork the repository.
2.  Create a feature branch: `git checkout -b feature-name`.
3.  Make your changes and commit them with clear messages.
4.  Push to your fork and create a pull request.

---

## 📄 License

This project is open source. Mapped under the MIT License.

