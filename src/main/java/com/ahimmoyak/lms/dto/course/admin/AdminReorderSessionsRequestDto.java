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
public class AdminReorderSessionsRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    @NotNull(message = "Session Index (fromSessionIndex) is a required field.")
    private int fromSessionIndex;

    @NotNull(message = "Session Index (toSessionIndex) is a required field.")
    private int toSessionIndex;

}
