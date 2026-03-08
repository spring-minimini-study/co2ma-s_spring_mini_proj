package com.co2ma.mini_proj.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "student")
@Data
public class Student {

    @Id
    private String student_id;

    private String name;
    private String phone;

    public Student() {}
}
