package com.ahimmoyak.lms.dto.course.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionPreviewDto {

    private String sessionTitle;
    private int sessionIndex;

}
