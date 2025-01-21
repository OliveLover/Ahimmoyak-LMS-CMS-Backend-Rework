package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.MessageResponseDto;
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
    public ResponseEntity<AdminManagedCoursesResponseDto> getManagedCourses() {
        return courseService.getManagedCourses();
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses")
    public ResponseEntity<AdminCreateCourseResponseDto> createCourse(@Valid @RequestBody AdminCreateCourseRequestDto requestDto) {
        return courseService.createCourse(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PutMapping("/api/v1/admin/courses")
    public ResponseEntity<MessageResponseDto> updateCourse(@Valid @RequestBody AdminUpdateCourseRequestDto requestDto) {
        return courseService.updateCourse(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @GetMapping("/api/v1/admin/courses/{courseId}")
    public ResponseEntity<AdminCourseDetailsResponseDto> getAdminCourseDetails(@PathVariable String courseId) {
        return courseService.getAdminCourseDetails(courseId);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @GetMapping("/api/v1/admin/courses/{courseId}/sessions")
    public ResponseEntity<AdminCourseSessionsResponseDto> getCourseSessions(@PathVariable String courseId) {
        return courseService.getCourseSessions(courseId);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<AdminCreateSessionResponseDto> createSession(@Valid @RequestBody AdminCreateSessionRequestDto requestDto) {
        return courseService.createSession(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PutMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<AdminUpdateSessionResponseDto> updateSession(@Valid @RequestBody AdminUpdateSessionRequestDto requestDto) {
        return courseService.updateSession(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PostMapping("/api/v1/admin/courses/sessions/contents")
    public ResponseEntity<AdminCreateContentResponseDto> createContent(@Valid @RequestBody AdminCreateContentRequestDto requestDto) {
        return courseService.createContent(requestDto);
    }

    @CrossOrigin(origins = {"http://localhost:5173", "https://d160mfz1jp4ygp.cloudfront.net"})
    @PutMapping("/api/v1/admin/courses/sessions/contents/quizzes")
    public ResponseEntity<AdminCreateQuizResponseDto> createQuiz(@Valid @RequestBody AdminCreateQuizRequestDto requestDto) {
        return courseService.createQuiz(requestDto);
    }

}
