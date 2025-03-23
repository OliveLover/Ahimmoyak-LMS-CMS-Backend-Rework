package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.course.user.UserCoursesResponseDto;
import com.ahimmoyak.lms.service.UserCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserCourseController {

    private final UserCourseService userCourseService;

    @GetMapping("/api/v1/courses")
    public ResponseEntity<UserCoursesResponseDto> getActiveNcsCourses() {
        return userCourseService.getActiveNcsCourses();
    }

    @GetMapping("/api/v1/courses/{code}")
    public ResponseEntity<UserCoursesResponseDto> getActiveCoursesByCode(@PathVariable String code) {
        return userCourseService.getActiveCoursesByCode(code);
    }

}
