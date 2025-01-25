package com.ahimmoyak.lms.dto.course;

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
public class AdminCourseDetailsResponseDto {

    private String courseId;
    private String courseTitle;
    private String courseIntroduce;
    private CourseStatus status;
    private LocalDate activeStartDate;
    private LocalDate activeEndDate;
    private String instructor;
    private String thumbnailPath;
    private CourseGrade grade;
    private NcsClassification ncsClassification;
    private int setDuration;
    private FundingType fundingType;
    private List<CardType> cardType;
    private List<SessionDto> sessions;

}