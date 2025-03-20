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
public class CoursesDto {

    private String courseTitle;
    private String thumbnailPath;
    private String ncsName;
    private String fundingTypeName;
    private List<String> cardTypeNames;

}
