package com.ahimmoyak.lms.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDto {

    private String error;
    private List<String> message;
    private LocalDateTime timestamp;
    private int status;

}
