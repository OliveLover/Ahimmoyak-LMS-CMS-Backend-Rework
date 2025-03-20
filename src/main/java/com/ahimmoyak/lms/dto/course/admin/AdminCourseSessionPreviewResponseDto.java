package com.ahimmoyak.lms.dto.course.admin;

import com.ahimmoyak.lms.dto.course.SessionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCourseSessionPreviewResponseDto {

    private SessionDto session;

}
