package com.ahimmoyak.lms.dto.course.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateQuizRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    @NotNull(message = "Quiz ID (quizId) is a required field.")
    private String quizId;

    private String question;
    private List<String> options;
    private int answer;
    private String explanation;

}
