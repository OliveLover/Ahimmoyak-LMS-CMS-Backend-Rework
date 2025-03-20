package com.ahimmoyak.lms.dto.course.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateSessionRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    private String sessionId;

    @NotBlank(message = "Session Title (sessionTitle) cannot be blank.")
    private String sessionTitle;

    @NotNull(message = "Session Index (sessionIndex) is a required field.")
    private int sessionIndex;

}
