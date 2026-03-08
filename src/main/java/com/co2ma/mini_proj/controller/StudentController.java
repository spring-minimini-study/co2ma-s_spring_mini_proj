package com.co2ma.mini_proj.controller;

import com.co2ma.mini_proj.entity.Student;
import com.co2ma.mini_proj.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @PostMapping
    public Student add(@RequestBody Student student){
        return studentRepository.save(student);
    }

    @GetMapping
    public List<Student> findAll(){
        return studentRepository.findAll();
    }

    @GetMapping("/search")
    public Student getOne(@RequestParam String student_id){
        return studentRepository.findById(student_id).orElse(null);
    }

}
