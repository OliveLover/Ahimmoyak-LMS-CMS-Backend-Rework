package com.ahimmoyak.lms.dto.course.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminReorderContentsRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    @NotNull(message = "Content Index (fromContentIndex) is a required field.")
    private int fromContentIndex;

    @NotNull(message = "Content Index (toContentIndex) is a required field.")
    private int toContentIndex;

}
