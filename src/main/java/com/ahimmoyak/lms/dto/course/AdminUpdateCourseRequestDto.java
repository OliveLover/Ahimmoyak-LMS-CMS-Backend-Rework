package com.ahimmoyak.lms.dto.course;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class AdminUpdateCourseRequestDto {

    private String courseId;

    @NotBlank(message = "Course title must not be empty or null")
    private String courseTitle;

    private String courseIntroduce;
    private CourseStatus status;
    private LocalDate activeStartDate;
    private LocalDate activeEndDate;
    private String instructor;
    private String thumbnailPath;
    private CourseGrade grade;
    private NCSClassification ncsClassification;

    @Min(value = 0, message = "The field 'setDuration' must be at least 0.")
    private int setDuration;

    private FundingType fundingType;
    private List<CardType> cardType;
    private LocalDate modifiedDate;

}
