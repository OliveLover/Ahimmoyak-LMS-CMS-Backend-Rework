package com.ahimmoyak.lms.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitiateMultipartUploadRequestDto {

    private String fileName;

}
