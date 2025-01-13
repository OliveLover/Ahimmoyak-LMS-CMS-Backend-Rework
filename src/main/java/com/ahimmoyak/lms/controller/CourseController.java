package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.CourseCreateRequestDto;
import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/api/v1/admin/courses")
    public ResponseEntity<MessageResponseDto> createCourse(@Valid @RequestBody CourseCreateRequestDto requestDto) {
        return courseService.createCourse(requestDto);
    }

}
