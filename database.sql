-- ==========================================
-- Smart Contact Manager Database Initialization
-- Database: smart_contact_manager
-- ==========================================

CREATE DATABASE IF NOT EXISTS smart_contact_manager;
USE smart_contact_manager;

-- 1. Create USER Table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `role` VARCHAR(100) DEFAULT 'ROLE_USER',
    `enabled` TINYINT(1) DEFAULT 1,
    `image_url` VARCHAR(255) DEFAULT 'default.png',
    `about` TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Create CONTACT Table
CREATE TABLE IF NOT EXISTS `contact` (
    `c_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `second_name` VARCHAR(255) DEFAULT NULL,
    `work` VARCHAR(255) DEFAULT NULL,
    `email` VARCHAR(255) DEFAULT NULL,
    `phone` VARCHAR(50) DEFAULT NULL,
    `image` VARCHAR(255) DEFAULT 'contact_default.png',
    `description` TEXT DEFAULT NULL,
    `category` VARCHAR(100) DEFAULT 'Personal',
    `favorite` TINYINT(1) DEFAULT 0,
    `address` TEXT DEFAULT NULL,
    `user_id` BIGINT DEFAULT NULL,
    CONSTRAINT `fk_contact_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Optimization Indexes for Advanced Search & Filters
CREATE INDEX idx_contact_user ON contact(user_id);
CREATE INDEX idx_contact_name ON contact(name);
CREATE INDEX idx_contact_email ON contact(email);
CREATE INDEX idx_contact_phone ON contact(phone);
CREATE INDEX idx_contact_work ON contact(work);
CREATE INDEX idx_contact_category ON contact(category);
CREATE INDEX idx_contact_favorite ON contact(favorite);

-- 4. Sample Admin/User Insertion
-- Note: The password below is BCrypt hashed for 'password123'
INSERT INTO `user` (`name`, `email`, `password`, `role`, `enabled`, `image_url`, `about`)
VALUES 
('Demo User', 'demo@contactmanager.com', '$2a$10$vDpx21F9yO9CqI6Vw.UxeOX7C1b3d.PqfK7tL38L8B2o6w4VfR9i.', 'ROLE_USER', 1, 'default.png', 'This is a demo user account for Smart Contact Manager app.')
ON DUPLICATE KEY UPDATE id=id;
