package com.ahimmoyak.lms.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuizDto {

    private String quizId;
    private int quizIndex;
    private String question;
    private List<String> options;
    private int answer;
    private String explanation;

}
