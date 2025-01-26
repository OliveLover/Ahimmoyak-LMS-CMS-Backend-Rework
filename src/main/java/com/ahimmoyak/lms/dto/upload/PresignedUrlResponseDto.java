package com.ahimmoyak.lms.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUrlResponseDto {

    private List<String> presignedUrls;

}
