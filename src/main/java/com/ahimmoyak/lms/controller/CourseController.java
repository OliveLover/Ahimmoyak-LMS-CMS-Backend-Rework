package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.course.*;
import com.ahimmoyak.lms.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @GetMapping("/api/v1/admin/courses")
    public ResponseEntity<ManagedCoursesResponseDto> getManagedCourses() {
        return courseService.getManagedCourses();
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses")
    public ResponseEntity<CourseCreateResponseDto> createCourse(@Valid @RequestBody CourseCreateRequestDto requestDto) {
        return courseService.createCourse(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @GetMapping("/api/v1/admin/courses/{courseId}")
    public ResponseEntity<CourseSessionsResponseDto> getCourseSessions(@PathVariable String courseId) {
        return courseService.getCourseSessions(courseId);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<SessionCreateResponseDto> createSession(@Valid @RequestBody SessionCreateRequestDto requestDto) {
        return courseService.createSession(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses/sessions/contents")
    public ResponseEntity<ContentCreateResponseDto> createContent(@Valid @RequestBody ContentCreateRequestDto requestDto) {
        return courseService.createContent(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PutMapping("/api/v1/admin/courses/sessions/contents/quizzes")
    public ResponseEntity<CreateQuizResponseDto> createQuiz(@Valid @RequestBody CreateQuizRequestDto requestDto) {
        return courseService.createQuiz(requestDto);
    }

}
