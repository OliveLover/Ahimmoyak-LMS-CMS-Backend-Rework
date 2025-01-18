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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    public ResponseEntity<AdminManagedCoursesResponseDto> getManagedCourses() {
        SdkIterable<Page<Course>> result = coursesTable.scan();

        List<AdminManagedCourseDto> courses = result.stream()
                .flatMap(page -> page.items().stream())
                .map(course -> {
                    LocalDate activeStartDate = course.getActiveStartDate();
                    LocalDate activeEndDate = course.getActiveEndDate();

                    int remainingDuration = calculateDaysDifference(activeEndDate);

                    return AdminManagedCourseDto.builder()
                            .courseId(course.getCourseId())
                            .courseTitle(course.getCourseTitle())
                            .status(course.getStatus())
                            .activeStartDate(activeStartDate)
                            .activeEndDate(activeEndDate)
                            .instructor(course.getInstructor())
                            .grade(course.getGrade())
                            .ncsClassification(course.getNcsClassification())
                            .setDuration(course.getSetDuration())
                            .remainingDuration(remainingDuration)
                            .fundingType(course.getFundingType())
                            .build();
                })
                .toList();

        AdminManagedCoursesResponseDto responseDto = AdminManagedCoursesResponseDto.builder()
                .courses(courses)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCourseDetailsResponseDto> getAdminCourseDetails(String courseId) {
        return null;
    }

    public ResponseEntity<AdminCourseCreateResponseDto> createCourse(AdminCourseCreateRequestDto requestDto) {
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
                .ncsClassification(requestDto.getNcsClassification())
                .setDuration(requestDto.getSetDuration())
                .fundingType(requestDto.getFundingType())
                .cardType(requestDto.getCardType())
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();
        coursesTable.putItem(course);

        AdminCourseCreateResponseDto responseDto = AdminCourseCreateResponseDto.builder()
                .courseId(courseId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCourseSessionsResponseDto> getCourseSessions(String courseId) {
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

        AdminCourseSessionsResponseDto responseDto = AdminCourseSessionsResponseDto.builder()
                .sessions(sessionDtos)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminSessionCreateResponseDto> createSession(AdminSessionCreateRequestDto requestDto) {
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

        AdminSessionCreateResponseDto responseDto = AdminSessionCreateResponseDto.builder()
                .sessionId(sessionId)
                .build();

        return ResponseEntity.ok(responseDto);

    }

    public ResponseEntity<AdminContentCreateResponseDto> createContent(@Valid AdminContentCreateRequestDto requestDto) {
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

        AdminContentCreateResponseDto responseDto = AdminContentCreateResponseDto.builder()
                .contentId(contentId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCreateQuizResponseDto> createQuiz(@Valid AdminCreateQuizRequestDto requestDto) {
        List<QuizDto> quizDtos = requestDto.getQuizzes();
        List<String> quizIds = new ArrayList<>();

        quizDtos.forEach(quizDto -> {
            String quizId = quizDto.getQuizId();

            if (quizDto.getQuizId() == null) {
                quizId = "quiz_" + UUID.randomUUID();
            }

            Quiz quiz = Quiz.builder()
                    .courseId(requestDto.getCourseId())
                    .quizId(quizId)
                    .contentId(requestDto.getContentId())
                    .quizIndex(quizDto.getQuizIndex())
                    .question(quizDto.getQuestion())
                    .options(quizDto.getOptions())
                    .answer(quizDto.getAnswer())
                    .explanation(quizDto.getExplanation())
                    .build();

            quizzesTable.putItem(quiz);

            quizIds.add(quizId);
        });

        AdminCreateQuizResponseDto responseDto = AdminCreateQuizResponseDto.builder()
                .quizzes(quizIds)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    private int calculateDaysDifference(LocalDate endDate) {
        if (endDate == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    private List<ContentDto> getContentByCourseIdAndSessionId(String courseId, String sessionId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Content>> result = contentsTable.query(queryRequest);

        return result.stream()
                .flatMap(page -> page.items().stream())
                .filter(content -> content.getSessionId().equals(sessionId))
                .map(content -> {
                    List<QuizDto> quizDtos = getQuizByCourseIdAndContentId(courseId, content.getContentId());
                    return mapToContentDto(content, quizDtos);
                })
                .toList();
    }

    private List<QuizDto> getQuizByCourseIdAndContentId(String courseId, String contentId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Quiz>> result = quizzesTable.query(queryRequest);

        return result.stream()
                .flatMap(page -> page.items().stream())
                .filter(quiz -> quiz.getContentId().equals(contentId))
                .map(this::mapToQuizDto)
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

    private ContentDto mapToContentDto(Content content, List<QuizDto> quizDtos) {
        return ContentDto.builder()
                .contentId(content.getContentId())
                .contentIndex(content.getContentIndex())
                .contentTitle(content.getContentTitle())
                .contentType(content.getContentType())
                .quiz(quizDtos)
                .build();
    }

    private QuizDto mapToQuizDto(Quiz quiz) {
        return QuizDto.builder()
                .quizId(quiz.getQuizId())
                .quizIndex(quiz.getQuizIndex())
                .question(quiz.getQuestion())
                .options(quiz.getOptions())
                .answer(quiz.getAnswer())
                .explanation(quiz.getExplanation())
                .build();
    }

}
