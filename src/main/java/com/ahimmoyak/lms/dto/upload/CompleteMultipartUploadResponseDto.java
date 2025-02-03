package com.ahimmoyak.lms.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteMultipartUploadResponseDto {

    private String fileId;
    private long fileSize;
    private String fileName;
    private int videoDuration;
    private String filePath;

}
