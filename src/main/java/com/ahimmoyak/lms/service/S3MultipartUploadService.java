package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.upload.*;
import com.ahimmoyak.lms.entity.Content;
import com.ahimmoyak.lms.entity.Course;
import com.ahimmoyak.lms.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ahimmoyak.lms.dto.upload.FileType.VIDEO;
import static com.ahimmoyak.lms.entity.Content.CONTENTS_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Course.COURSES_TABLE_SCHEMA;

@Slf4j
@Service
public class S3MultipartUploadService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final DynamoDbTable<Content> contentsTable;
    private final DynamoDbTable<Course> coursesTable;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Autowired
    public S3MultipartUploadService(S3Client s3Client, S3Presigner s3Presigner, DynamoDbEnhancedClient enhancedClient) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.contentsTable = enhancedClient.table("contents", CONTENTS_TABLE_SCHEMA);
        this.coursesTable = enhancedClient.table("courses", COURSES_TABLE_SCHEMA);
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
                .fileId(fileId)
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

    public ResponseEntity<CompleteMultipartUploadResponseDto> completeMultipartUpload(CompleteMultipartUploadRequestDto requestDto) {
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

        CompleteMultipartUploadResponseDto responseDto = VIDEO.equals(requestDto.getFileType())
                ? updateContentMetaInfo(requestDto)
                : updateCourseMetaInfo(requestDto);

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

    private CompleteMultipartUploadResponseDto updateContentMetaInfo(CompleteMultipartUploadRequestDto requestDto) {
        String courseId = requestDto.getCourseId();
        String contentId = requestDto.getContentId();
        String fileId = requestDto.getFileId();
        long fileSize = requestDto.getFileSize();
        String fileName = requestDto.getFileName();
        String fileKey = requestDto.getFileKey();
        FileType fileType = requestDto.getFileType();
        int videoDuration = requestDto.getVideoDuration();
        String filePath = generateS3FileUrl(requestDto.getFileKey());
        
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)
                        .sortValue(contentId)))
                .build();

        SdkIterable<Page<Content>> contentResult = contentsTable.query(queryRequest);
        Content content = contentResult.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElse(null);

        String existingFileKey = null;

        if (content != null) {
            existingFileKey = content.getFileKey();
        }

        if (existingFileKey != null) {
            deleteFileFromS3(existingFileKey);
        }

        Content existingContent = contentsTable.getItem(r -> r.key(k -> k
                .partitionValue(courseId)
                .sortValue(contentId)
        ));

        if (existingContent == null) {
            throw new NotFoundException("The course or content with the given IDs does not exist.");
        }

        Content updatedContent = existingContent.toBuilder()
                .fileId(fileId)
                .fileSize(fileSize)
                .fileName(fileName)
                .fileKey(fileKey)
                .fileType(fileType)
                .videoDuration(videoDuration)
                .videoPath(filePath)
                .build();

        UpdateItemEnhancedRequest<Content> enhancedRequest = UpdateItemEnhancedRequest.builder(Content.class)
                .item(updatedContent)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(content_id)")
                        .build())
                .build();

        contentsTable.updateItem(enhancedRequest);

        return CompleteMultipartUploadResponseDto.builder()
                .fileId(fileId)
                .fileName(fileName)
                .fileSize(fileSize)
                .filePath(filePath)
                .videoDuration(videoDuration)
                .build();
    }

    private CompleteMultipartUploadResponseDto updateCourseMetaInfo(CompleteMultipartUploadRequestDto requestDto) {
        String courseId = requestDto.getCourseId();
        String fileId = requestDto.getFileId();
        long fileSize = requestDto.getFileSize();
        String fileName = requestDto.getFileName();
        String fileKey = requestDto.getFileKey();
        String filePath = generateS3FileUrl(fileKey);

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Course>> courseResult = coursesTable.query(queryRequest);
        Course course = courseResult.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElse(null);

        String existingFileKey = null;

        if (course != null) {
            existingFileKey = course.getFileKey();
        }

        if (existingFileKey != null) {
            deleteFileFromS3(existingFileKey);
        }

        Course existingCourse = coursesTable.getItem(r -> r.key(k -> k
                .partitionValue(courseId)
        ));

        if (existingCourse == null) {
            throw new NotFoundException("The course with the given IDs does not exist.");
        }

        Course updatedCourse = existingCourse.toBuilder()
                .thumbnailId(fileId)
                .thumbnailPath(filePath)
                .thumbnailSize(fileSize)
                .thumbnailName(fileName)
                .fileKey(fileKey)
                .build();

        UpdateItemEnhancedRequest<Course> enhancedRequest = UpdateItemEnhancedRequest.builder(Course.class)
                .item(updatedCourse)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(course_id)")
                        .build())
                .build();

        coursesTable.updateItem(enhancedRequest);

        return CompleteMultipartUploadResponseDto.builder()
                .fileId(fileId)
                .fileName(fileName)
                .fileSize(fileSize)
                .filePath(filePath)
                .build();
    }

    private void deleteFileFromS3(String fileKey) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build());
    }

    private String generateS3FileUrl(String fileKey) {
        return "https://" + bucketName + ".s3." + awsRegion + ".amazonaws.com/" + fileKey;
    }

}