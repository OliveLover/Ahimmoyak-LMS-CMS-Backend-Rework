package com.ahimmoyak.lms.dto.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PresignedUrlRequestDto {

    @NotNull(message = "Upload ID (uploadId) is a required field.")
    private String uploadId;

    @NotBlank(message = "File key is required.")
    @Size(max = 255, message = "fileKey must not exceed 255 characters.")
    private String fileKey;

    @NotNull(message = "File size must be provided.")
    private Long fileSize;

}
