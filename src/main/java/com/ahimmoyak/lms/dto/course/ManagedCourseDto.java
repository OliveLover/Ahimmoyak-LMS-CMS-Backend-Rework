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
public class ManagedCourseDto {

    private String courseId;
    private String courseTitle;
    private String status;
    private LocalDate activeStartDate;
    private LocalDate activeEndDate;
    private String instructor;
    private String grade;
    private String category;
    private int setDuration;
    private int remainingDuration;
    private String fundingType;

}
