-- Competition system tables
-- H2 Compatible Version with MySQL Mode
-- Creates tables for competitions, judges, entries, and ratings

-- Competitions table
CREATE TABLE competitions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    model_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    deadline TIMESTAMP NOT NULL,
    status ENUM('ACTIVE', 'ENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (model_id) REFERENCES evaluation_models(id),
    FOREIGN KEY (creator_id) REFERENCES users(id)
);

-- Competition judges table
CREATE TABLE competition_judges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    judge_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (competition_id) REFERENCES competitions(id) ON DELETE CASCADE,
    FOREIGN KEY (judge_id) REFERENCES users(id),
    UNIQUE (competition_id, judge_id)
);

-- Competition entries table
CREATE TABLE competition_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    entry_name VARCHAR(100) NOT NULL,
    description TEXT,
    file_path VARCHAR(255),
    display_order INT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (competition_id) REFERENCES competitions(id) ON DELETE CASCADE
);

-- Competition ratings table
CREATE TABLE competition_ratings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    competition_id BIGINT NOT NULL,
    entry_id BIGINT NOT NULL,
    judge_id BIGINT NOT NULL,
    parameter_id BIGINT NOT NULL,
    score DOUBLE NOT NULL,
    note TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (competition_id) REFERENCES competitions(id),
    FOREIGN KEY (entry_id) REFERENCES competition_entries(id),
    FOREIGN KEY (judge_id) REFERENCES users(id),
    FOREIGN KEY (parameter_id) REFERENCES evaluation_parameters(id),
    UNIQUE (entry_id, judge_id, parameter_id)
);