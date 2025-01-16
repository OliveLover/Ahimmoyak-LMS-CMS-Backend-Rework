package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.course.*;
import com.ahimmoyak.lms.entity.Content;
import com.ahimmoyak.lms.entity.Course;
import com.ahimmoyak.lms.entity.Session;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.LocalDate;
import java.util.UUID;

import static com.ahimmoyak.lms.entity.Content.CONTENTS_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Course.COURSES_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Session.SESSIONS_TABLE_SCHEMA;

@Service
public class CourseService {

    private final DynamoDbTable<Course> coursesTable;
    private final DynamoDbTable<Session> sessionsTable;
    private final DynamoDbTable<Content> contentsTable;

    @Autowired
    public CourseService(DynamoDbEnhancedClient enhancedClient) {
        this.coursesTable = enhancedClient.table("courses", COURSES_TABLE_SCHEMA);
        this.sessionsTable = enhancedClient.table("sessions", SESSIONS_TABLE_SCHEMA);
        this.contentsTable = enhancedClient.table("contents", CONTENTS_TABLE_SCHEMA);
    }

    public ResponseEntity<CourseCreateResponseDto> createCourse(CourseCreateRequestDto requestDto) {
        String courseId = requestDto.getCourseId();

        if (requestDto.getCourseId() == null) {
            courseId = "course_" + UUID.randomUUID();
        }

        Course course = Course.builder()
                .courseId(courseId)
                .courseTitle(requestDto.getCourseTitle())
                .courseIntroduce(requestDto.getCourseIntroduce())
                .status(requestDto.getStatus())
                .activeStartDate(requestDto.getActiveStartDate())
                .activeEndDate(requestDto.getActiveEndDate())
                .instructor(requestDto.getInstructor())
                .thumbnailPath(requestDto.getThumbnailPath())
                .grade(requestDto.getGrade())
                .category(requestDto.getCategory())
                .setDuration(requestDto.getSetDuration())
                .fundingType(requestDto.getFundingType())
                .cardType(requestDto.getCardType())
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();
        coursesTable.putItem(course);

        CourseCreateResponseDto responseDto = CourseCreateResponseDto.builder()
                .courseId(courseId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<SessionCreateResponseDto> createSession(SessionCreateRequestDto requestDto) {
        String sessionId = requestDto.getSessionId();

        if (requestDto.getSessionId() == null) {
            sessionId = "session_" + UUID.randomUUID();
        }

        Session session = Session.builder()
                .courseId(requestDto.getCourseId())
                .sessionId(sessionId)
                .sessionTitle(requestDto.getSessionTitle())
                .sessionIndex(requestDto.getSessionIndex())
                .build();

        sessionsTable.putItem(session);

        SessionCreateResponseDto responseDto = SessionCreateResponseDto.builder()
                .sessionId(sessionId)
                .build();

        return ResponseEntity.ok(responseDto);

    }

    public ResponseEntity<ContentCreateResponseDto> createContent(@Valid ContentCreateRequestDto requestDto) {
        String contentId = requestDto.getContentId();

        if (requestDto.getContentId() == null) {
            contentId = "content_" + UUID.randomUUID();
        }

        Content content = Content.builder()
                .courseId(requestDto.getCourseId())
                .sessionId(requestDto.getSessionId())
                .contentId(contentId)
                .contentTitle(requestDto.getContentTitle())
                .contentType(requestDto.getContentType())
                .contentIndex(requestDto.getContentIndex())
                .build();

        contentsTable.putItem(content);

        ContentCreateResponseDto responseDto = ContentCreateResponseDto.builder()
                .contentId(contentId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

}
