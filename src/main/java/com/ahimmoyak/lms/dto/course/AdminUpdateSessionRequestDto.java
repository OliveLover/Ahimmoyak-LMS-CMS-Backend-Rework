package com.ahimmoyak.lms.dto.course;

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
public class AdminUpdateSessionRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    @NotNull(message = "Session ID (sessionId) is a required field.")
    private String sessionId;

    @NotBlank(message = "Session Title (sessionTitle) cannot be blank.")
    private String sessionTitle;

}
