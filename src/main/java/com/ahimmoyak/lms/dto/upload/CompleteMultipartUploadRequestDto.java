package com.ahimmoyak.lms.dto.upload;

import jakarta.validation.constraints.*;
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

    @NotNull(message = "Course ID (courseId) is a required field.")
    private String courseId;

    private String contentId;

    @NotBlank(message = "File Key is required.")
    @Size(max = 255, message = "fileKey must not exceed 255 characters.")
    private String fileKey;

    @NotNull(message = "Upload ID (uploadId) is a required field.")
    private String uploadId;

    @NotNull(message = "file ID (fileId) is a required field.")
    private String fileId;

    @Positive(message = "File size must be a positive number.")
    private long fileSize;

    @NotBlank(message = "File Name is required.")
    private String fileName;

    @NotNull(message = "File Type is required.")
    private FileType fileType;

    private int videoDuration;

    @NotEmpty(message = "completedParts list must not be empty.")
    private List<CompletedPartDto> completedParts;

}
