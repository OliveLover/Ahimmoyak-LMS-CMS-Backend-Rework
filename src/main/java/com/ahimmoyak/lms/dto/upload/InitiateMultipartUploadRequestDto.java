package com.ahimmoyak.lms.dto.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InitiateMultipartUploadRequestDto {

    @NotBlank(message = "File Key Prefix is required.")
    @Size(max = 255, message = "fileKeyPrefix must not exceed 255 characters.")
    private String fileKeyPrefix;

    @NotBlank(message = "File Key Postfix is required.")
    @Size(max = 50, message = "fileKeyPostfix must not exceed 255 characters.")
    private String fileKeyPostfix;

}
