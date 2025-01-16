package com.ahimmoyak.lms.dto.course;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionDto {

    private String sessionId;
    private String sessionTitle;
    private int sessionIndex;
    private List<ContentDto> contents;

}
