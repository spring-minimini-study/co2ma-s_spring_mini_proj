package com.co2ma.mini_proj.student.dto;

public record StudentRequest(
        String studentId,
        String name,
        String phone
) {}