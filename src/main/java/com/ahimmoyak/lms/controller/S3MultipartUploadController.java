package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.upload.InitiateMultipartUploadRequestDto;
import com.ahimmoyak.lms.dto.upload.InitiateMultipartUploadResponseDto;
import com.ahimmoyak.lms.service.S3MultipartUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class S3MultipartUploadController {

    private final S3MultipartUploadService s3MultipartUploadService;

    @PostMapping("/api/v1/s3/initiate")
    public ResponseEntity<InitiateMultipartUploadResponseDto> initiateMultipartUpload(@RequestBody InitiateMultipartUploadRequestDto requestDto) {
        return s3MultipartUploadService.initiateMultipartUpload(requestDto);
    }

    @PutMapping("/api/v1/s3/complete")
    public ResponseEntity<MessageResponseDto> completeMultipartUpload(@RequestParam String fileName, @RequestParam String uploadId, @RequestBody List<CompletedPart> completedParts) {
        return s3MultipartUploadService.completeMultipartUpload(fileName, uploadId, completedParts);
    }

    @DeleteMapping("/api/v1/s3/abort")
    public ResponseEntity<MessageResponseDto> abortMultipartUpload(@RequestParam String fileName, @RequestParam String uploadId) {
        return s3MultipartUploadService.abortMultipartUpload(fileName, uploadId);
    }

}
