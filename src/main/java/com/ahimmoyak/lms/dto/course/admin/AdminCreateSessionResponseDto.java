package com.ahimmoyak.lms.dto.course.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateSessionResponseDto {

    private String sessionId;

}
