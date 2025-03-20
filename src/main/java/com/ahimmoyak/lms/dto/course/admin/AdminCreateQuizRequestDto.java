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
public class AdminCreateQuizRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    @NotNull(message = "Content ID (contentId) is a required field.")
    private String contentId;

    private String quizId;
    private int quizIndex;
    private String question;
    private List<String> options;
    private int answer;
    private String explanation;

}
