package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.course.admin.*;
import com.ahimmoyak.lms.service.AdminCourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminCourseController {

    private final AdminCourseService adminCourseService;

    @GetMapping("/api/v1/admin/courses")
    public ResponseEntity<AdminManagedCoursesResponseDto> getManagedCourses() {
        return adminCourseService.getManagedCourses();
    }

    @PostMapping("/api/v1/admin/courses")
    public ResponseEntity<AdminCreateCourseResponseDto> createCourse(@Valid @RequestBody AdminCreateCourseRequestDto requestDto) {
        return adminCourseService.createCourse(requestDto);
    }

    @PutMapping("/api/v1/admin/courses")
    public ResponseEntity<MessageResponseDto> updateCourse(@Valid @RequestBody AdminUpdateCourseRequestDto requestDto) {
        return adminCourseService.updateCourse(requestDto);
    }

    @DeleteMapping("/api/v1/admin/courses")
    public ResponseEntity<MessageResponseDto> deleteCourses(@RequestBody AdminDeleteCoursesRequestDto requestDto) {
        return adminCourseService.deleteCourses(requestDto);
    }

    @GetMapping("/api/v1/admin/courses/{courseId}")
    public ResponseEntity<AdminCourseDetailsResponseDto> getAdminCourseDetails(@PathVariable String courseId) {
        return adminCourseService.getAdminCourseDetails(courseId);
    }

    @GetMapping("/api/v1/admin/courses/{courseId}/sessions")
    public ResponseEntity<AdminCourseSessionInfoResponseDto> getCourseSessionInfo(@PathVariable String courseId) {
        return adminCourseService.getCourseSessionInfo(courseId);
    }

    @GetMapping("/api/v1/admin/courses/{courseId}/sessions/{sessionId}/preview")
    public ResponseEntity<AdminCourseSessionPreviewResponseDto> getCourseSessionPreview(@PathVariable String courseId, @PathVariable String sessionId) {
        return adminCourseService.getCourseSessionPreview(courseId, sessionId);
    }

    @PostMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<AdminCreateSessionResponseDto> createSession(@Valid @RequestBody AdminCreateSessionRequestDto requestDto) {
        return adminCourseService.createSession(requestDto);
    }

    @PutMapping("/api/v1/admin/courses/sessions")
    public ResponseEntity<MessageResponseDto> updateSession(@Valid @RequestBody AdminUpdateSessionRequestDto requestDto) {
        return adminCourseService.updateSession(requestDto);
    }

    @PatchMapping("/api/v1/admin/courses/sessions/reorder")
    public ResponseEntity<MessageResponseDto> reorderSessions(@Valid @RequestBody AdminReorderSessionsRequestDto requestDto) {
        return adminCourseService.reorderSessions(requestDto);
    }

    @DeleteMapping("/api/v1/admin/courses/{courseId}/sessions/{sessionId}")
    public ResponseEntity<MessageResponseDto> deleteSession(@PathVariable String courseId, @PathVariable String sessionId) {
        return adminCourseService.deleteSession(courseId, sessionId);
    }

    @PostMapping("/api/v1/admin/courses/sessions/contents")
    public ResponseEntity<AdminCreateContentResponseDto> createContent(@Valid @RequestBody AdminCreateContentRequestDto requestDto) {
        return adminCourseService.createContent(requestDto);
    }

    @PutMapping("/api/v1/admin/courses/sessions/contents")
    public ResponseEntity<MessageResponseDto> updateContent(@Valid @RequestBody AdminUpdateContentRequestDto requestDto) {
        return adminCourseService.updateContent(requestDto);
    }

    @PatchMapping("/api/v1/admin/courses/sessions/contents/reorder")
    public ResponseEntity<MessageResponseDto> reorderContents(@Valid @RequestBody AdminReorderContentsRequestDto requestDto) {
        return adminCourseService.reorderContents(requestDto);
    }

    @DeleteMapping("/api/v1/admin/courses/{courseId}/sessions/{sessionId}/contents/{contentId}")
    public ResponseEntity<MessageResponseDto> deleteContent(@PathVariable String courseId, @PathVariable String sessionId, @PathVariable String contentId) {
        return adminCourseService.deleteContent(courseId, sessionId, contentId);
    }

    @PostMapping("/api/v1/admin/courses/sessions/contents/quizzes")
    public ResponseEntity<AdminCreateQuizResponseDto> createQuiz(@Valid @RequestBody AdminCreateQuizRequestDto requestDto) {
        return adminCourseService.createQuiz(requestDto);
    }

    @PutMapping("/api/v1/admin/courses/sessions/contents/quizzes/{quizId}")
    public ResponseEntity<MessageResponseDto> updateQuiz(@Valid @RequestBody AdminUpdateQuizRequestDto requestDto) {
        return adminCourseService.updateQuiz(requestDto);
    }

    @DeleteMapping("/api/v1/admin/courses/{courseId}/sessions/{sessionId}/contents/{contentId}/quizzes/{quizId}")
    public ResponseEntity<MessageResponseDto> deleteQuiz(@PathVariable String courseId, @PathVariable String quizId) {
        return adminCourseService.deleteQuiz(courseId, quizId);
    }

}
