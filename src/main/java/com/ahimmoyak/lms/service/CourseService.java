package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.course.*;
import com.ahimmoyak.lms.entity.Content;
import com.ahimmoyak.lms.entity.Course;
import com.ahimmoyak.lms.entity.Quiz;
import com.ahimmoyak.lms.entity.Session;
import com.ahimmoyak.lms.exception.NotFoundException;
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

    public ResponseEntity<AdminCreateCourseResponseDto> createCourse(AdminCreateCourseRequestDto requestDto) {
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

        AdminCreateCourseResponseDto responseDto = AdminCreateCourseResponseDto.builder()
                .courseId(courseId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> updateCourse(AdminUpdateCourseRequestDto requestDto) {

        String courseId = requestDto.getCourseId();

        Course existingCourse = coursesTable.getItem(r -> r.key(k -> k.partitionValue(courseId)));
        if (existingCourse == null) {
            throw new NotFoundException("The course with the given courseId does not exist.");
        }

        Course updatedCourse = Course.builder()
                .courseId(existingCourse.getCourseId())
                .courseTitle(requestDto.getCourseTitle() != null ? requestDto.getCourseTitle() : existingCourse.getCourseTitle())
                .courseIntroduce(requestDto.getCourseIntroduce() != null ? requestDto.getCourseIntroduce() : existingCourse.getCourseIntroduce())
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : existingCourse.getStatus())
                .activeStartDate(requestDto.getActiveStartDate() != null ? requestDto.getActiveStartDate() : existingCourse.getActiveStartDate())
                .activeEndDate(requestDto.getActiveEndDate() != null ? requestDto.getActiveEndDate() : existingCourse.getActiveEndDate())
                .instructor(requestDto.getInstructor() != null ? requestDto.getInstructor() : existingCourse.getInstructor())
                .thumbnailPath(requestDto.getThumbnailPath() != null ? requestDto.getThumbnailPath() : existingCourse.getThumbnailPath())
                .grade(requestDto.getGrade() != null ? requestDto.getGrade() : existingCourse.getGrade())
                .ncsClassification(requestDto.getNcsClassification() != null ? requestDto.getNcsClassification() : existingCourse.getNcsClassification())
                .setDuration(requestDto.getSetDuration() != 0 ? requestDto.getSetDuration() : existingCourse.getSetDuration())
                .fundingType(requestDto.getFundingType() != null ? requestDto.getFundingType() : existingCourse.getFundingType())
                .cardType(requestDto.getCardType() != null ? requestDto.getCardType() : existingCourse.getCardType())
                .createdDate(existingCourse.getCreatedDate())
                .modifiedDate(LocalDate.now())
                .build();

        coursesTable.putItem(updatedCourse);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Course updated successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCourseDetailsResponseDto> getAdminCourseDetails(String courseId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Course>> courseResult = coursesTable.query(queryRequest);
        Course course = courseResult.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Course not found with ID: " + courseId));

        SdkIterable<Page<Session>> sessionResult = sessionsTable.query(queryRequest);
        List<SessionDto> sessionDtos = sessionResult.stream()
                .flatMap(page -> page.items().stream())
                .map(session -> {
                    List<ContentDto> contentDtos = getContentByCourseIdAndSessionId(courseId, session.getSessionId());
                    return mapToSessionDto(session, contentDtos);
                })
                .toList();

        AdminCourseDetailsResponseDto responseDto = AdminCourseDetailsResponseDto.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getCourseTitle())
                .courseIntroduce(course.getCourseIntroduce())
                .status(course.getStatus())
                .activeStartDate(course.getActiveStartDate())
                .activeEndDate(course.getActiveEndDate())
                .instructor(course.getInstructor())
                .thumbnailPath(course.getThumbnailPath())
                .grade(course.getGrade())
                .ncsClassification(course.getNcsClassification())
                .setDuration(course.getSetDuration())
                .fundingType(course.getFundingType())
                .cardType(course.getCardType())
                .sessions(sessionDtos)
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

    public ResponseEntity<AdminCreateSessionResponseDto> createSession(AdminCreateSessionRequestDto requestDto) {
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

        AdminCreateSessionResponseDto responseDto = AdminCreateSessionResponseDto.builder()
                .sessionId(sessionId)
                .build();

        return ResponseEntity.ok(responseDto);

    }

    public ResponseEntity<AdminUpdateSessionResponseDto> updateSession(AdminUpdateSessionRequestDto requestDto) {


        return null;
    }

    public ResponseEntity<AdminCreateContentResponseDto> createContent(AdminCreateContentRequestDto requestDto) {
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

        AdminCreateContentResponseDto responseDto = AdminCreateContentResponseDto.builder()
                .contentId(contentId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCreateQuizResponseDto> createQuiz(AdminCreateQuizRequestDto requestDto) {
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
                .quizzes(quizDtos)
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
