package com.co2ma.mini_proj.student;

import com.co2ma.mini_proj.student.dto.StudentRequest;
import com.co2ma.mini_proj.student.dto.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    @Transactional
    public StudentResponse save(StudentRequest dto){
        Student student = Student.builder()
                .studentId(dto.studentId())
                .name(dto.name())
                .phone(dto.phone())
                .build();
        Student saved = studentRepository.save(student);
        return StudentResponse.from(saved);
    }

    public List<StudentResponse> findAll(){
        return studentRepository.findAll().stream()
                .map(StudentResponse::from)
                .toList();
    }

    public StudentResponse findById(String id){
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다"));
        return StudentResponse.from(student);
    }

}
