CREATE TABLE IF NOT EXISTS parse_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    volume_regex VARCHAR(500),
    chapter_regex VARCHAR(500) NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS novel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255),
    status VARCHAR(32) NOT NULL DEFAULT 'NOT_STARTED',
    last_analyzed_chapter_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS volume (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    idx INT NOT NULL,
    title VARCHAR(255),
    summary TEXT
);

CREATE TABLE IF NOT EXISTS chapter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    volume_id BIGINT NOT NULL,
    novel_id BIGINT NOT NULL,
    idx INT NOT NULL,
    title VARCHAR(255),
    raw_content MEDIUMTEXT,
    analysis_result MEDIUMTEXT,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    word_count INT
);

CREATE TABLE IF NOT EXISTS novel_outline (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL UNIQUE,
    outline_json MEDIUMTEXT
);

CREATE TABLE IF NOT EXISTS character
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(32),
    relationship_to_protagonist VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);

CREATE TABLE IF NOT EXISTS weapon
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    grade VARCHAR(32),
    significance VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);

CREATE TABLE IF NOT EXISTS faction
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(32),
    stance_toward_protagonist VARCHAR(32),
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);

CREATE TABLE IF NOT EXISTS spirit_beast
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    level VARCHAR(32),
    significance VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);

CREATE TABLE IF NOT EXISTS technique
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    grade VARCHAR(32),
    significance VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);

CREATE TABLE IF NOT EXISTS spirit_herb
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    grade VARCHAR(32),
    significance VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);

CREATE TABLE IF NOT EXISTS elixir
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    grade VARCHAR(32),
    significance VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);

CREATE TABLE IF NOT EXISTS location
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    novel_id BIGINT NOT NULL,
    first_chapter_id BIGINT,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(32),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_novel_name (novel_id, name)
);
