package com.ahimmoyak.lms.dto.course.user;

import com.ahimmoyak.lms.dto.course.CoursesDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCoursesResponseDto {

    private List<CoursesDto> courses;

}
