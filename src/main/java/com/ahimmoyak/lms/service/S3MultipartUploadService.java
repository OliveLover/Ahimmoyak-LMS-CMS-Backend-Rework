package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.upload.InitiateMultipartUploadRequestDto;
import com.ahimmoyak.lms.dto.upload.InitiateMultipartUploadResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;

@Slf4j
@Service
public class S3MultipartUploadService {

    private final S3Client s3Client;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Autowired
    public S3MultipartUploadService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public ResponseEntity<InitiateMultipartUploadResponseDto> initiateMultipartUpload(InitiateMultipartUploadRequestDto requestDto) {
        CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(requestDto.getFileName())
                .contentType("application/octet-stream")
                .build();

        CreateMultipartUploadResponse uploadResponse = s3Client.createMultipartUpload(request);

        InitiateMultipartUploadResponseDto responseDto = InitiateMultipartUploadResponseDto.builder()
                .uploadId(uploadResponse.uploadId())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> completeMultipartUpload(String fileName, String uploadId, List<CompletedPart> completedParts) {
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .uploadId(uploadId)
                .multipartUpload(completedMultipartUpload)
                .build();

        CompleteMultipartUploadResponse completeResponse = s3Client.completeMultipartUpload(completeRequest);

        log.info("Upload completed successfully with ETag: {}", completeResponse.eTag());

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Multipart upload completed successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> abortMultipartUpload(String fileName, String uploadId) {
        AbortMultipartUploadRequest abortMultipartUploadRequest = AbortMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .uploadId(uploadId)
                .build();

        log.info("Aborting multipart upload for file: {}, uploadId: {}", fileName, uploadId);

        s3Client.abortMultipartUpload(abortMultipartUploadRequest);

        log.info("Multipart upload aborted successfully for file: {}, uploadId: {}", fileName, uploadId);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .build();

        return ResponseEntity.ok(responseDto);
    }

}