CREATE TABLE member (
    id          BIGSERIAL,
    login_id    VARCHAR(50) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    nickname    VARCHAR(50) NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    role        VARCHAR(50) NOT NULL,
    CONSTRAINT  pk_member PRIMARY KEY (id)
);