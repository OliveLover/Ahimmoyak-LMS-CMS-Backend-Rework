package com.ahimmoyak.lms.dto.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteMultipartUploadRequestDto {

    @NotBlank(message = "File Key is required.")
    @Size(max = 255, message = "fileKey must not exceed 255 characters.")
    private String fileKey;

    @NotBlank(message = "uploadId is required.")
    private String uploadId;

    @NotEmpty(message = "completedParts list must not be empty.")
    private List<CompletedPartDto> completedParts;

}
