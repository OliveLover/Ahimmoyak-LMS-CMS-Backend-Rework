package com.ahimmoyak.lms.dto;

import com.ahimmoyak.lms.entity.Course;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CourseCreateRequestDto {

    private String courseId;
    private String courseTitle;

    private String courseIntroduce;
    private String status;

    @NotNull(message = "The field 'activeStartDate' is missing.")
    private LocalDate activeStartDate;

    @NotNull(message = "The field 'activeStartDate' is missing.")
    private LocalDate activeEndDate;

    private String instructor;
    private String thumbnailPath;
    private String grade;
    private String category;

    @Min(value = 0, message = "The field 'setDuration' must be at least 0.")
    private int setDuration;

    private String fundingType;
    private List<String> cardType;
    private LocalDate createdDate;
    private LocalDate modifiedDate;

    public Course toEntity() {
        return Course.builder()
                .courseId(this.courseId)
                .title(this.courseTitle)
                .introduce(this.courseIntroduce)
                .status(this.status)
                .activeStartDate(this.activeStartDate)
                .activeEndDate(this.activeEndDate)
                .instructor(this.instructor)
                .thumbnailPath(this.thumbnailPath)
                .grade(this.grade)
                .category(this.category)
                .setDuration(this.setDuration)
                .fundingType(this.fundingType)
                .cardType(this.cardType)
                .createdDate(this.createdDate != null ? this.createdDate : LocalDate.now())
                .modifiedDate(this.modifiedDate != null ? this.modifiedDate : LocalDate.now())
                .build();
    }

}
