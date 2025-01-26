package com.ahimmoyak.lms.dto.upload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompletedPartDto {

    @JsonProperty("eTag")
    @NotBlank(message = "eTag is required.")
    private String eTag;

    @Positive(message = "partNumber must be a positive number.")
    private int partNumber;

}
