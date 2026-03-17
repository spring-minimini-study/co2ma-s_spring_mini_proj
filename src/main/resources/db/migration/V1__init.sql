CREATE TABLE student (
    student_id   VARCHAR(255) Not Null,
    name        VARCHAR(100),
    phone       VARCHAR(20),
    CONSTRAINT  pk_student PRIMARY KEY (student_id)
)