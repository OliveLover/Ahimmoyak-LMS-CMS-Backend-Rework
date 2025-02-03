package com.ahimmoyak.lms.controller;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.upload.*;
import com.ahimmoyak.lms.service.S3MultipartUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://www.ahimmoyak.shop"})
public class S3MultipartUploadController {

    private final S3MultipartUploadService s3MultipartUploadService;

    @PostMapping("/api/v1/s3/initiate")
    public ResponseEntity<InitiateMultipartUploadResponseDto> initiateMultipartUpload(@Valid @RequestBody InitiateMultipartUploadRequestDto requestDto) {
        return s3MultipartUploadService.initiateMultipartUpload(requestDto);
    }

    @PostMapping("/api/v1/s3/presigned-url")
    public ResponseEntity<PresignedUrlResponseDto> createPresignedUrls(@Valid @RequestBody PresignedUrlRequestDto requestDto) {
        return s3MultipartUploadService.createPresignedUrls(requestDto);
    }

    @PutMapping("/api/v1/s3/complete")
    public ResponseEntity<CompleteMultipartUploadResponseDto> completeMultipartUpload(@Valid @RequestBody CompleteMultipartUploadRequestDto requestDto) {
        return s3MultipartUploadService.completeMultipartUpload(requestDto);
    }

    @DeleteMapping("/api/v1/s3/abort")
    public ResponseEntity<MessageResponseDto> abortMultipartUpload(@RequestParam String fileName, @RequestParam String uploadId) {
        return s3MultipartUploadService.abortMultipartUpload(fileName, uploadId);
    }

}
