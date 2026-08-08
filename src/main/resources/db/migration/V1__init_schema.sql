-- CareerCompass MySQL schema — Flyway migration V1
-- Applied automatically on first boot when the database is empty.
-- This is the canonical schema aligned with all JPA entities.

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    state VARCHAR(100),
    category ENUM('GENERAL', 'EWS', 'OBC', 'SC', 'ST'),
    gender ENUM('MALE', 'FEMALE', 'GENDER_NEUTRAL'),
    role ENUM('STUDENT', 'ADMIN') NOT NULL DEFAULT 'STUDENT',
    enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS exams (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_type ENUM('JEE_MAIN', 'JEE_ADVANCED', 'TS_EAMCET', 'AP_EAMCET', 'BITSAT', 'CUET', 'NEET_UG', 'NEET_PG', 'INI_CET', 'STATE_MEDICAL') NOT NULL UNIQUE,
    description TEXT NOT NULL,
    counselling_body VARCHAR(150),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_exam_type (exam_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS colleges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    college_type ENUM('NIT', 'IIIT', 'GFTI', 'STATE_COLLEGE', 'PRIVATE_COLLEGE', 'UNIVERSITY', 'DEEMED_UNIVERSITY', 'GOVERNMENT_MEDICAL', 'PRIVATE_MEDICAL', 'DENTAL_COLLEGE', 'AYUSH_COLLEGE') NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    fees DECIMAL(15, 2),
    average_package DECIMAL(15, 2),
    highest_package DECIMAL(15, 2),
    placement_percentage INT,
    nirf_rank INT,
    website VARCHAR(255),
    hostel_available BOOLEAN DEFAULT FALSE,
    campus_size INT,
    description TEXT,
    counselling_type VARCHAR(100),
    seat_intake INT,
    bond_details VARCHAR(255),
    stipend_details VARCHAR(255),
    hospital_attached BOOLEAN DEFAULT FALSE,
    annual_patient_flow VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_college_name (name),
    INDEX idx_college_type (college_type),
    INDEX idx_college_state (state)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS branches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    branch_code VARCHAR(10) NOT NULL UNIQUE,
    degree_type ENUM('BTECH', 'MBBS', 'BDS', 'BAMS', 'BHMS', 'BUMS', 'BVSC', 'BACHELOR', 'INTEGRATED_MASTER', 'DIPLOMA') NOT NULL,
    duration INT NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_branch_name (name),
    INDEX idx_branch_code (branch_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cutoffs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exam_id BIGINT NOT NULL,
    college_id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    category ENUM('GENERAL', 'EWS', 'OBC', 'SC', 'ST') NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'GENDER_NEUTRAL') NOT NULL,
    quota ENUM('HOME_STATE', 'ALL_INDIA', 'FOREIGN') NOT NULL,
    round_number INT NOT NULL,
    opening_rank INT NOT NULL,
    closing_rank INT NOT NULL,
    year INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cutoff_exam FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    CONSTRAINT fk_cutoff_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE CASCADE,
    CONSTRAINT fk_cutoff_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE CASCADE,
    INDEX idx_exam_college (exam_id, college_id),
    INDEX idx_year_round (year, round_number),
    INDEX idx_closing_rank (closing_rank),
    UNIQUE KEY unique_cutoff_record (exam_id, college_id, branch_id, category, gender, quota, round_number, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS prediction_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    exam_id BIGINT NOT NULL,
    `rank` INT NOT NULL,
    category ENUM('GENERAL', 'EWS', 'OBC', 'SC', 'ST') NOT NULL,
    gender ENUM('MALE', 'FEMALE', 'GENDER_NEUTRAL') NOT NULL,
    quota ENUM('HOME_STATE', 'ALL_INDIA', 'FOREIGN') NOT NULL,
    home_state VARCHAR(100),
    preferred_branch VARCHAR(100),
    preferred_college_type VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prediction_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_prediction_exam FOREIGN KEY (exam_id) REFERENCES exams(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_exam_id (exam_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS wishlist (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    college_id BIGINT NOT NULL,
    branch_id BIGINT,
    notes TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wishlist_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_college FOREIGN KEY (college_id) REFERENCES colleges(id) ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_branch FOREIGN KEY (branch_id) REFERENCES branches(id) ON DELETE SET NULL,
    INDEX idx_wishlist_user_id (user_id),
    INDEX idx_wishlist_college_id (college_id),
    INDEX idx_wishlist_branch_id (branch_id),
    UNIQUE KEY unique_wishlist_record (user_id, college_id, branch_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
