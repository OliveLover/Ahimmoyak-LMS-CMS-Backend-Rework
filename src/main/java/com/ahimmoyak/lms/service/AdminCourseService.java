package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.dto.course.*;
import com.ahimmoyak.lms.entity.Content;
import com.ahimmoyak.lms.entity.Course;
import com.ahimmoyak.lms.entity.Quiz;
import com.ahimmoyak.lms.entity.Session;
import com.ahimmoyak.lms.exception.BadRequestException;
import com.ahimmoyak.lms.exception.NotFoundException;
import com.ahimmoyak.lms.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.ahimmoyak.lms.entity.Content.CONTENTS_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Course.COURSES_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Quiz.QUIZZES_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Session.SESSIONS_TABLE_SCHEMA;

@Slf4j
@Service
public class AdminCourseService {

    private final DynamoDbTable<Course> coursesTable;
    private final DynamoDbTable<Session> sessionsTable;
    private final DynamoDbTable<Content> contentsTable;
    private final DynamoDbTable<Quiz> quizzesTable;
    private final S3MultipartUploadService s3MultipartUploadService;

    @Autowired
    public AdminCourseService(DynamoDbEnhancedClient enhancedClient, S3MultipartUploadService s3MultipartUploadService) {
        this.coursesTable = enhancedClient.table("courses", COURSES_TABLE_SCHEMA);
        this.sessionsTable = enhancedClient.table("sessions", SESSIONS_TABLE_SCHEMA);
        this.contentsTable = enhancedClient.table("contents", CONTENTS_TABLE_SCHEMA);
        this.quizzesTable = enhancedClient.table("quizzes", QUIZZES_TABLE_SCHEMA);
        this.s3MultipartUploadService = s3MultipartUploadService;
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

        Course updatedCourse = existingCourse.toBuilder()
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

        UpdateItemEnhancedRequest<Course> enhancedRequest = UpdateItemEnhancedRequest.builder(Course.class)
                .item(updatedCourse)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(course_id)")
                        .build())
                .build();

        coursesTable.updateItem(enhancedRequest);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Course updated successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> deleteCourses(@Valid AdminDeleteCoursesRequestDto requestDto) {
        List<String> courseIds = requestDto.getCourseIds();

        for (String courseId : courseIds) {
            QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                    .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                    .build();

            SdkIterable<Page<Course>> result = coursesTable.query(queryRequest);

            Course existingCourse = result.stream()
                    .flatMap(page -> page.items().stream())
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));

            deleteFileFromS3IfExists(existingCourse.getFileKey());

            coursesTable.deleteItem(existingCourse);
        }

        for (String courseId : courseIds) {

            deleteAllSession(courseId);
        }

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Courses deleted successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCourseDetailsResponseDto> getAdminCourseDetails(String courseId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Course>> result = coursesTable.query(queryRequest);
        Course course = result.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Course not found with ID: " + courseId));

        AdminCourseDetailsResponseDto responseDto = AdminCourseDetailsResponseDto.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getCourseTitle())
                .courseIntroduce(course.getCourseIntroduce())
                .status(course.getStatus())
                .activeStartDate(course.getActiveStartDate())
                .activeEndDate(course.getActiveEndDate())
                .instructor(course.getInstructor())
                .thumbnailPath(course.getThumbnailPath())
                .thumbnailName(course.getThumbnailName())
                .thumbnailSize(course.getThumbnailSize())
                .grade(course.getGrade())
                .ncsClassification(course.getNcsClassification())
                .setDuration(course.getSetDuration())
                .fundingType(course.getFundingType())
                .cardType(course.getCardType())
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCourseSessionInfoResponseDto> getCourseSessionInfo(String courseId) {
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

        AdminCourseSessionInfoResponseDto responseDto = AdminCourseSessionInfoResponseDto.builder()
                .sessions(sessionDtos)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCourseSessionPreviewResponseDto> getCourseSessionPreview(String courseId, String sessionId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)
                        .sortValue(sessionId)))
                .build();

        SdkIterable<Page<Session>> result = sessionsTable.query(queryRequest);
        SessionDto sessionDto = result.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .map(session -> {
                    List<ContentDto> contentDtos = getContentByCourseIdAndSessionId(courseId, session.getSessionId());
                    return mapToSessionDto(session, contentDtos);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found for courseId: " + courseId + ", sessionId: " + sessionId)
                );

        AdminCourseSessionPreviewResponseDto responseDto = AdminCourseSessionPreviewResponseDto.builder()
                .session(sessionDto)
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

    public ResponseEntity<MessageResponseDto> updateSession(AdminUpdateSessionRequestDto requestDto) {
        String courseId = requestDto.getCourseId();
        String sessionId = requestDto.getSessionId();

        Session existingSession = sessionsTable.getItem(r -> r.key(k -> k
                .partitionValue(courseId)
                .sortValue(sessionId)
        ));

        if (existingSession == null) {
            throw new NotFoundException("The course or session with the given IDs does not exist.");
        }

        Session updatedSession = existingSession.toBuilder()
                .sessionTitle(requestDto.getSessionTitle())
                .build();

        UpdateItemEnhancedRequest<Session> enhancedRequest = UpdateItemEnhancedRequest.builder(Session.class)
                .item(updatedSession)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(session_id)")
                        .build())
                .build();

        sessionsTable.updateItem(enhancedRequest);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Session updated successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> reorderSessions(AdminReorderSessionsRequestDto requestDto) {
        String courseId = requestDto.getCourseId();
        int fromSessionIndex = requestDto.getFromSessionIndex();
        int toSessionIndex = requestDto.getToSessionIndex();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        List<Session> sessions = new ArrayList<>(sessionsTable.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .sorted(Comparator.comparing(Session::getSessionIndex))
                .toList());

        if (sessions.isEmpty()) {
            throw new NotFoundException("No sessions found for the given courseId.");
        }

        Session movedSession = sessions.stream()
                .filter(session -> session.getSessionIndex() == fromSessionIndex)
                .findFirst()
                .orElse(null);

        if (movedSession == null) {
            throw new BadRequestException("Invalid fromSessionIndex.");
        }

        sessions.remove(movedSession);

        sessions.add(toSessionIndex - 1, movedSession);

        for (int i = 0; i < sessions.size(); i++) {
            sessions.get(i).setSessionIndex(i + 1);
            sessionsTable.updateItem(sessions.get(i));
        }

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Sessions reordered successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> deleteSession(String courseId, String sessionId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Session>> result = sessionsTable.query(queryRequest);
        Session existingSession = result.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException("Session not found for courseId: " + courseId + ", sessionId: " + sessionId)
                );

        deleteAllContent(courseId, sessionId);

        sessionsTable.deleteItem(existingSession);

        reorderSessionIndexAfterDelete(courseId);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Session deleted successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
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

    public ResponseEntity<MessageResponseDto> updateContent(AdminUpdateContentRequestDto requestDto) {
        String courseId = requestDto.getCourseId();
        String contentId = requestDto.getContentId();

        Content existingContent = contentsTable.getItem(r -> r.key(k -> k
                .partitionValue(courseId)
                .sortValue(contentId)
        ));

        if (existingContent == null) {
            throw new NotFoundException("The course or content with the given IDs does not exist.");
        }

        Content updatedContent = existingContent.toBuilder()
                .contentTitle(requestDto.getContentTitle())
                .contentType(requestDto.getContentType())
                .build();

        UpdateItemEnhancedRequest<Content> enhancedRequest = UpdateItemEnhancedRequest.builder(Content.class)
                .item(updatedContent)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(content_id)")
                        .build())
                .build();

        contentsTable.updateItem(enhancedRequest);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Session updated successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> reorderContents(@Valid AdminReorderContentsRequestDto requestDto) {
        String courseId = requestDto.getCourseId();
        int fromContentIndex = requestDto.getFromContentIndex();
        int toContentIndex = requestDto.getToContentIndex();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        List<Content> contents = new ArrayList<>(contentsTable.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .sorted(Comparator.comparing(Content::getContentIndex))
                .toList());

        if (contents.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponseDto("No sessions found for the given courseId."));
        }

        Content movedContent = contents.stream()
                .filter(content -> content.getContentIndex() == fromContentIndex)
                .findFirst()
                .orElse(null);

        if (movedContent == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponseDto("Invalid fromContentIndex."));
        }

        contents.remove(movedContent);

        contents.add(toContentIndex - 1, movedContent);

        for (int i = 0; i < contents.size(); i++) {
            contents.get(i).setContentIndex(i + 1);
            contentsTable.updateItem(contents.get(i));
        }

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Contents reordered successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> deleteContent(String courseId, String sessionId, String contentId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)
                        .sortValue(contentId))
                )
                .build();

        SdkIterable<Page<Content>> result = contentsTable.query(queryRequest);
        Content existingContent = result.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException("Content not found for courseId: " + courseId + ", contentId: " + contentId)
                );

        deleteAllQuiz(courseId, contentId);

        if (existingContent.getFileKey() != null && !existingContent.getFileKey().isEmpty()) {
            s3MultipartUploadService.deleteFileFromS3(existingContent.getFileKey());
        }

        contentsTable.deleteItem(existingContent);

        reorderContentIndexAfterDelete(courseId, sessionId);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Content deleted successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<AdminCreateQuizResponseDto> createQuiz(AdminCreateQuizRequestDto requestDto) {
        String quizId = requestDto.getQuizId();

        if (quizId == null) {
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

        AdminCreateQuizResponseDto responseDto = AdminCreateQuizResponseDto.builder()
                .quizId(quizId)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> updateQuiz(AdminUpdateQuizRequestDto requestDto) {
        String courseId = requestDto.getCourseId();
        String quizId = requestDto.getQuizId();

        Quiz existingQuiz = quizzesTable.getItem(r -> r.key(k -> k
                .partitionValue(courseId)
                .sortValue(quizId)
        ));

        if (existingQuiz == null) {
            throw new NotFoundException("The course or content with the given IDs does not exist.");
        }

        Quiz updatedQuiz = existingQuiz.toBuilder()
                .question(requestDto.getQuestion())
                .options(requestDto.getOptions())
                .answer(requestDto.getAnswer())
                .explanation(requestDto.getExplanation())
                .build();

        UpdateItemEnhancedRequest<Quiz> enhancedRequest = UpdateItemEnhancedRequest.builder(Quiz.class)
                .item(updatedQuiz)
                .conditionExpression(Expression.builder()
                        .expression("attribute_exists(course_id)")
                        .build())
                .build();

        quizzesTable.updateItem(enhancedRequest);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Quiz updated successfully.")
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<MessageResponseDto> deleteQuiz(String courseId, String quizId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)
                        .sortValue(quizId))
                )
                .build();

        SdkIterable<Page<Quiz>> result = quizzesTable.query(queryRequest);
        Quiz existingQuiz = result.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException("Quiz not found for courseId: " + courseId + ", quizId: " + quizId)
                );

        if (existingQuiz == null) {
            throw new NotFoundException("The course or content with the given IDs does not exist.");
        }

        quizzesTable.deleteItem(existingQuiz);

        reorderQuizIndexAfterDelete(courseId);

        MessageResponseDto responseDto = MessageResponseDto.builder()
                .message("Quiz deleted successfully.")
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
                .videoPath(content.getVideoPath())
                .fileId(content.getFileId())
                .fileType(content.getFileType())
                .videoDuration(content.getVideoDuration())
                .fileSize(content.getFileSize())
                .fileName(content.getFileName())
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

    private void deleteAllSession(String courseId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Session>> result = sessionsTable.query(queryRequest);
        List<Session> sessions = result.stream()
                .flatMap(page -> page.items().stream())
                .toList();

        if (sessions.isEmpty()) {
            return;
        }

        List<String> sessionIds = sessions.stream()
                .map(Session::getSessionId)
                .toList();

        for (String sessionId : sessionIds) {
            deleteAllContent(courseId, sessionId);
        }

        for (Session session : sessions) {
            sessionsTable.deleteItem(session);
        }

    }

    private void reorderSessionIndexAfterDelete(String courseId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        List<Session> sessions = sessionsTable.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .sorted(Comparator.comparing(Session::getSessionIndex))
                .toList();

        if (sessions.isEmpty()) {
            return;
        }

        List<Session> updatedSessions = new ArrayList<>();

        int sessionsSize = sessions.size();
        for (int i = 0; i < sessionsSize; i++) {
            Session session = sessions.get(i);
            session.setSessionIndex(i + 1);
            updatedSessions.add(session);
        }

        updatedSessions.forEach(sessionsTable::updateItem);
    }

    private void reorderContentIndexAfterDelete(String courseId, String sessionId) {
        Expression expression = Expression.builder()
                .expression("#sessionId = :sessionId")
                .putExpressionName("#sessionId", "session_id")
                .putExpressionValue(":sessionId", AttributeValue.builder()
                        .s(sessionId)
                        .build())
                .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .filterExpression(expression)
                .build();

        List<Content> contents = contentsTable.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .sorted(Comparator.comparing(Content::getContentIndex))
                .toList();

        if (contents.isEmpty()) {
            return;
        }

        List<Content> updatedContents = new ArrayList<>();

        int contentsSize = contents.size();
        for (int i = 0; i < contentsSize; i++) {
            Content content = contents.get(i);
            content.setContentIndex(i + 1);
            updatedContents.add(content);
        }

        updatedContents.forEach(contentsTable::updateItem);
    }

    private void reorderQuizIndexAfterDelete(String courseId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        List<Quiz> quizzes = quizzesTable.query(queryRequest)
                .stream()
                .flatMap(page -> page.items().stream())
                .sorted(Comparator.comparing(Quiz::getQuizIndex))
                .toList();

        if (quizzes.isEmpty()) {
            return;
        }

        List<Quiz> updatedQuizzes = new ArrayList<>();

        int quizzesSize = quizzes.size();
        for (int i = 0; i < quizzesSize; i++) {
            Quiz quiz = quizzes.get(i);
            quiz.setQuizIndex(i + 1);
            updatedQuizzes.add(quiz);
        }

        updatedQuizzes.forEach(quizzesTable::updateItem);
    }

    private void deleteAllContent(String courseId, String sessionId) {
        Expression expression = Expression.builder()
                .expression("#sessionId = :sessionId")
                .putExpressionName("#sessionId", "session_id")
                .putExpressionValue(":sessionId", AttributeValue.builder()
                        .s(sessionId)
                        .build())
                .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .filterExpression(expression)
                .build();

        SdkIterable<Page<Content>> result = contentsTable.query(queryRequest);

        List<Content> contents = result.stream()
                .flatMap(page -> page.items().stream())
                .toList();

        if (contents.isEmpty()) {
            return;
        }

        contents.forEach(content -> {
            Content existingContent = findContentById(courseId, content.getContentId());

            deleteAllQuiz(courseId, content.getContentId());

            deleteFileFromS3IfExists(existingContent.getFileKey());

            contentsTable.deleteItem(content);
        });
    }

    private void deleteAllQuiz(String courseId, String contentId) {
        Expression expression = Expression.builder()
                .expression("#contentId = :contentId")
                .putExpressionName("#contentId", "content_id")
                .putExpressionValue(":contentId", AttributeValue.builder()
                        .s(contentId)
                        .build())
                .build();

        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .filterExpression(expression)
                .build();

        SdkIterable<Page<Quiz>> result = quizzesTable.query(queryRequest);

        if (result.stream().flatMap(page -> page.items().stream()).findFirst().isEmpty()) {
            return;
        }

        result.stream()
                .flatMap(page -> page.items().stream())
                .forEach(quizzesTable::deleteItem);
    }

    private Content findContentById(String courseId, String contentId) {
        QueryEnhancedRequest singleQueryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)
                        .sortValue(contentId)))
                .build();

        SdkIterable<Page<Content>> singleResult = contentsTable.query(singleQueryRequest);

        return singleResult.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        "Content not found for courseId: " + courseId + ", contentId: " + contentId));
    }

    private void deleteFileFromS3IfExists(String fileKey) {
        if (fileKey != null && !fileKey.isEmpty()) {
            s3MultipartUploadService.deleteFileFromS3(fileKey);
        }
    }
}
