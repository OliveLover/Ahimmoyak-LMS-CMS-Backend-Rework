package com.ahimmoyak.lms.dto.course.admin;

import com.ahimmoyak.lms.dto.course.ContentType;
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
public class AdminCreateContentRequestDto {

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    @NotNull(message = "Session ID (sessionId) is a required field.")
    private String sessionId;

    private String contentId;

    @NotBlank(message = "Content Title (contentTitle) cannot be blank.")
    private String contentTitle;

    @NotNull(message = "Content type is a required field.")
    private ContentType contentType;

    @NotNull(message = "Content Index (contentIndex) is a required field.")
    private int contentIndex;

}
