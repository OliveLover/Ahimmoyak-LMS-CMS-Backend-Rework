package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.course.CourseCreateRequestDto;
import com.ahimmoyak.lms.dto.course.CourseCreateResponseDto;
import com.ahimmoyak.lms.dto.course.SessionCreateRequestDto;
import com.ahimmoyak.lms.dto.course.SessionCreateResponseDto;
import com.ahimmoyak.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses")
    public ResponseEntity<CourseCreateResponseDto> createCourse(@Valid @RequestBody CourseCreateRequestDto requestDto) {
        return courseService.createCourse(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<SessionCreateResponseDto> createSession(@Valid @RequestBody SessionCreateRequestDto requestDto) {
        return courseService.createSession(requestDto);
    }

}
