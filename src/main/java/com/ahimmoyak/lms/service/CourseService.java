package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.course.*;
import com.ahimmoyak.lms.entity.Content;
import com.ahimmoyak.lms.entity.Course;
import com.ahimmoyak.lms.entity.Quiz;
import com.ahimmoyak.lms.entity.Session;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.ahimmoyak.lms.entity.Content.CONTENTS_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Course.COURSES_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Quiz.QUIZZES_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Session.SESSIONS_TABLE_SCHEMA;

@Service
public class CourseService {

    private final DynamoDbTable<Course> coursesTable;
    private final DynamoDbTable<Session> sessionsTable;
    private final DynamoDbTable<Content> contentsTable;
    private final DynamoDbTable<Quiz> quizzesTable;

    @Autowired
    public CourseService(DynamoDbEnhancedClient enhancedClient) {
        this.coursesTable = enhancedClient.table("courses", COURSES_TABLE_SCHEMA);
        this.sessionsTable = enhancedClient.table("sessions", SESSIONS_TABLE_SCHEMA);
        this.contentsTable = enhancedClient.table("contents", CONTENTS_TABLE_SCHEMA);
        this.quizzesTable = enhancedClient.table("quiz", QUIZZES_TABLE_SCHEMA);
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

    public ResponseEntity<CourseSessionsResponseDto> getCourseSessions(String courseId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Session>> result = sessionsTable.query(queryRequest);
        List<SessionDto> sessionDtos = result.stream()
                .flatMap(page -> page.items().stream())
                .map(session -> {
                    List<ContentDto> contentDtos = getContentByCourseIdAndSessionId(courseId, session.getSessionId());
                    return mapToSessionDto(session, contentDtos);
                })
                .toList();

        CourseSessionsResponseDto responseDto = CourseSessionsResponseDto.builder()
                .sessions(sessionDtos)
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

    public ResponseEntity<CreateQuizResponseDto> createQuiz(@Valid CreateQuizRequestDto requestDto) {
        String quizId = requestDto.getQuizId();

        if (requestDto.getQuizId() == null) {
            quizId = "quiz_" + UUID.randomUUID();
        }

        Quiz quiz = Quiz.builder()
                .courseId(requestDto.getCourseId())
                .quizId(quizId)
                .contentId(requestDto.getContentId())
                .quizIndex(requestDto.getQuizIndex())
                .question(requestDto.getQuestion())
                .options(requestDto.getOptions())
                .answer(requestDto.getAnswer())
                .explanation(requestDto.getExplanation())
                .build();

        quizzesTable.putItem(quiz);

        CreateQuizResponseDto responseDto = CreateQuizResponseDto.builder()
                .quizId(quizId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    private List<ContentDto> getContentByCourseIdAndSessionId(String courseId, String sessionId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Content>> result = contentsTable.query(queryRequest);

        return result.stream()
                .flatMap(page -> page.items().stream())
                .filter(content -> content.getSessionId().equals(sessionId))
                .map(this::mapToContentDto)
                .toList();
    }

    private SessionDto mapToSessionDto(Session session, List<ContentDto> contentDtos) {
        return SessionDto.builder()
                .sessionId(session.getSessionId())
                .sessionTitle(session.getSessionTitle())
                .sessionIndex(session.getSessionIndex())
                .contents(contentDtos)
                .build();
    }

    private ContentDto mapToContentDto(Content content) {
        return ContentDto.builder()
                .contentId(content.getContentId())
                .contentIndex(content.getContentIndex())
                .contentTitle(content.getContentTitle())
                .contentType(content.getContentType())
                .build();
    }
}
