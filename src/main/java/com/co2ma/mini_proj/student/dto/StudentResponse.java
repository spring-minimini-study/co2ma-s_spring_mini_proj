package com.co2ma.mini_proj.student.dto;

import com.co2ma.mini_proj.student.Student;

public record StudentResponse(
        String studentId,
        String name,
        String phone
) {
    public static StudentResponse from(Student student) {
        return new StudentResponse(
                student.getStudentId(),
                student.getName(),
                student.getPhone()
        );
    }
}
