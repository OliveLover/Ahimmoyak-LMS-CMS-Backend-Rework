package com.ahimmoyak.lms.dto.course;


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
public class CreateQuizRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    @NotNull(message = "Content ID (contentId) is a required field.")
    private String contentId;

    private String quizId;

    @NotNull(message = "Quiz Index (quizIndex) is a required field.")
    private int quizIndex;

    private String question;
    private List<String> options;

    @NotNull(message = "Answer (answer) is a required field.")
    private Integer answer;

    private String explanation;

}
