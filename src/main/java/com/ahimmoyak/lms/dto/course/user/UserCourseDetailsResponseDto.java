package com.ahimmoyak.lms.dto.course.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCourseDetailsResponseDto {

    private String courseId;
    private String courseTitle;
    private String courseIntroduce;
    private String instructor;
    private String thumbnailPath;
    private LocalDate startDate;
    private LocalDate endDate;
    private int setDuration;
    private String fundingTypeName;
    private List<String> cardTypeNames;
    private List<SessionPreviewDto> sessionPreviews;

}
