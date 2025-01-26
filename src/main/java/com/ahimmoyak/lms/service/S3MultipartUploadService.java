package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.upload.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class S3MultipartUploadService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Autowired
    public S3MultipartUploadService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    public ResponseEntity<InitiateMultipartUploadResponseDto> initiateMultipartUpload(InitiateMultipartUploadRequestDto requestDto) {
        String fileKeyPrefix = requestDto.getFileKeyPrefix();
        String fileKeyPostfix = requestDto.getFileKeyPostfix();
        String fileId = "file_" + UUID.randomUUID();
        String fileKey = fileKeyPrefix + fileId + fileKeyPostfix;

        CreateMultipartUploadRequest request = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType("application/octet-stream")
                .build();

        CreateMultipartUploadResponse uploadResponse = s3Client.createMultipartUpload(request);

        InitiateMultipartUploadResponseDto responseDto = InitiateMultipartUploadResponseDto.builder()
                .uploadId(uploadResponse.uploadId())
                .fileKey(fileKey)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<PresignedUrlResponseDto> createPresignedUrls(PresignedUrlRequestDto requestDto) {
        String uploadId = requestDto.getUploadId();
        String fileKey = requestDto.getFileKey();
        long fileSize = requestDto.getFileSize();
        long partSize = 5L * 1024 * 1024;
        int partCount = (int) ((fileSize + partSize - 1) / partSize);
        List<String> presignedUrls = new ArrayList<>();

        for (int partNumber = 1; partNumber <= partCount; partNumber++) {
            String presignedUrl = createMultipartPresignedUrl(fileKey, uploadId, partNumber);

            presignedUrls.add(presignedUrl);
        }

        PresignedUrlResponseDto responseDto = PresignedUrlResponseDto.builder()
                .presignedUrls(presignedUrls)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> completeMultipartUpload(CompleteMultipartUploadRequestDto requestDto) {
        List<CompletedPart> completedParts = convertToCompletedParts(requestDto.getCompletedParts());

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedParts)
                .build();

        CompleteMultipartUploadRequest completeRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(requestDto.getFileKey())
                .uploadId(requestDto.getUploadId())
                .multipartUpload(completedMultipartUpload)
                .build();

        s3Client.completeMultipartUpload(completeRequest);

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

    private String createMultipartPresignedUrl(String fileKey, String uploadId, int partNumber) {
        AwsRequestOverrideConfiguration requestConfig = AwsRequestOverrideConfiguration.builder()
                .putRawQueryParameter("uploadId", uploadId)
                .putRawQueryParameter("partNumber", String.valueOf(partNumber))
                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .overrideConfiguration(requestConfig)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(p -> p
                .putObjectRequest(putObjectRequest)
                .signatureDuration(Duration.ofMinutes(10)));

        return presignedRequest.url().toString();
    }

    private List<CompletedPart> convertToCompletedParts(List<CompletedPartDto> dtoList) {
        return dtoList.stream()
                .map(dto ->
                        CompletedPart.builder()
                                .eTag(dto.getETag())
                                .partNumber(dto.getPartNumber())
                                .build()
                )
                .toList();
    }

}