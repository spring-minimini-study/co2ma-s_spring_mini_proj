package com.co2ma.mini_proj.student;

import com.co2ma.mini_proj.student.dto.StudentRequest;
import com.co2ma.mini_proj.student.dto.StudentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // 생성자 주입 자동화
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public StudentResponse add(@RequestBody StudentRequest dto){
        return studentService.save(dto);
    }

    @GetMapping
    public List<StudentResponse> findAll(){
        return studentService.findAll();
    }

    @GetMapping("/search")
    public StudentResponse getOne(@RequestParam String studentId){
        return studentService.findById(studentId);
    }
}