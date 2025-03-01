package com.ahimmoyak.lms.dto.course;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminDeleteCoursesRequestDto {

    @NotEmpty(message = "courseIds must contain at least one item.")
    private List<String> courseIds;

}
