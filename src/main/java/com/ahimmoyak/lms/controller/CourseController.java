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
@CrossOrigin(origins = {"http://localhost:5173", "https://www.ahimmoyak.shop"})
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/api/v1/admin/courses")
    public ResponseEntity<AdminManagedCoursesResponseDto> getManagedCourses() {
        return courseService.getManagedCourses();
    }

    @PostMapping("/api/v1/admin/courses")
    public ResponseEntity<AdminCreateCourseResponseDto> createCourse(@Valid @RequestBody AdminCreateCourseRequestDto requestDto) {
        return courseService.createCourse(requestDto);
    }

    @PutMapping("/api/v1/admin/courses")
    public ResponseEntity<MessageResponseDto> updateCourse(@Valid @RequestBody AdminUpdateCourseRequestDto requestDto) {
        return courseService.updateCourse(requestDto);
    }

    @GetMapping("/api/v1/admin/courses/{courseId}")
    public ResponseEntity<AdminCourseDetailsResponseDto> getAdminCourseDetails(@PathVariable String courseId) {
        return courseService.getAdminCourseDetails(courseId);
    }

    @GetMapping("/api/v1/admin/courses/{courseId}/sessions/{sessionId}/preview")
    public ResponseEntity<AdminCourseSessionInfoResponseDto> getCourseSessionInfo(@PathVariable String courseId, @PathVariable String sessionId) {
        return courseService.getCourseSessionInfo(courseId, sessionId);
    }

    @PostMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<AdminCreateSessionResponseDto> createSession(@Valid @RequestBody AdminCreateSessionRequestDto requestDto) {
        return courseService.createSession(requestDto);
    }

    @PutMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<MessageResponseDto> updateSession(@Valid @RequestBody AdminUpdateSessionRequestDto requestDto) {
        return courseService.updateSession(requestDto);
    }

    @PostMapping("/api/v1/admin/courses/sessions/contents")
    public ResponseEntity<AdminCreateContentResponseDto> createContent(@Valid @RequestBody AdminCreateContentRequestDto requestDto) {
        return courseService.createContent(requestDto);
    }

    @PutMapping("/api/v1/admin/courses/sessions/contents")
    public ResponseEntity<MessageResponseDto> updateContent(@Valid @RequestBody AdminUpdateContentRequestDto requestDto) {
        return courseService.updateContent(requestDto);
    }

    @PutMapping("/api/v1/admin/courses/sessions/contents/quizzes")
    public ResponseEntity<AdminCreateQuizResponseDto> createQuiz(@Valid @RequestBody AdminCreateQuizRequestDto requestDto) {
        return courseService.createQuiz(requestDto);
    }

}
