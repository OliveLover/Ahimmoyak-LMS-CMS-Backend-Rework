package com.ahimmoyak.lms.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminManagedCourseDto {

    private String courseId;
    private String courseTitle;
    private CourseStatus status;
    private LocalDate activeStartDate;
    private LocalDate activeEndDate;
    private String instructor;
    private CourseGrade grade;
    private NcsClassification ncsClassification;
    private int setDuration;
    private int remainingDuration;
    private FundingType fundingType;

}
