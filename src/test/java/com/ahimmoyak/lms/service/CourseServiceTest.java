package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.course.*;
import com.ahimmoyak.lms.entity.Content;
import com.ahimmoyak.lms.entity.Course;
import com.ahimmoyak.lms.entity.Quiz;
import com.ahimmoyak.lms.entity.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.ahimmoyak.lms.dto.course.CardType.CORPORATE_TRAINING_SUPPORT_CARD;
import static com.ahimmoyak.lms.dto.course.CardType.NATIONAL_EMPLOYMENT_SUPPORT_CARD;
import static com.ahimmoyak.lms.dto.course.ContentType.VIDEO;
import static com.ahimmoyak.lms.dto.course.CourseGrade.A;
import static com.ahimmoyak.lms.dto.course.CourseStatus.ACTIVE;
import static com.ahimmoyak.lms.dto.course.FundingType.REFUNDABLE;
import static com.ahimmoyak.lms.dto.course.NCSClassification.BUSINESS_MANAGEMENT;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseServiceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final DynamoDbTable<Course> coursesTable;
    private final DynamoDbTable<Session> sessionsTable;
    private final DynamoDbTable<Content> contentsTable;
    private final DynamoDbTable<Quiz> quizzesTable;

    private String courseId;
    private String sessionId;
    private String contentId;
    private List<String> quizIds = new ArrayList<>();

    @Autowired
    public CourseServiceTest(DynamoDbEnhancedClient enhancedClient) {
        this.coursesTable = enhancedClient.table("courses", Course.COURSES_TABLE_SCHEMA);
        this.sessionsTable = enhancedClient.table("sessions", Session.SESSIONS_TABLE_SCHEMA);
        this.contentsTable = enhancedClient.table("contents", Content.CONTENTS_TABLE_SCHEMA);
        this.quizzesTable = enhancedClient.table("quiz", Quiz.QUIZZES_TABLE_SCHEMA);
    }

    @AfterEach
    void deleteCourseData() {
        if (courseId != null) {
            Key key = Key.builder()
                    .partitionValue(courseId)
                    .build();
            coursesTable.deleteItem(key);
        }
    }

    @AfterEach
    void deleteSessionData() {
        if (courseId != null && sessionId != null) {
            Key key = Key.builder()
                    .partitionValue(courseId)
                    .sortValue(sessionId)
                    .build();
            sessionsTable.deleteItem(key);
        }
    }

    @AfterEach
    void deleteContentData() {
        if (courseId != null && contentId != null) {
            Key key = Key.builder()
                    .partitionValue(courseId)
                    .sortValue(contentId)
                    .build();
            contentsTable.deleteItem(key);
        }
    }

    @AfterEach
    void deleteQuizData() {
        if (courseId != null && !quizIds.isEmpty()) {
            for (String quizId : quizIds) {
                Key key = Key.builder()
                        .partitionValue(courseId)
                        .sortValue(quizId)
                        .build();
                quizzesTable.deleteItem(key);
            }
        }
        quizIds.clear();
    }

    @Test
    @DisplayName("훈련과정 생성하면 DynamoDb로 저장하고 200 OK로 응답한다.")
    void createCourse_shouldReturnSuccessMessage() throws Exception {
        // given
        courseId = "coures_" + UUID.randomUUID();

        AdminCourseCreateRequestDto requestDto = AdminCourseCreateRequestDto.builder()
                .courseId(courseId)
                .courseTitle("Course Title")
                .courseIntroduce("Course Introduction")
                .status(ACTIVE)
                .activeStartDate(LocalDate.of(2025, 1, 1))
                .activeEndDate(LocalDate.of(2025, 12, 31))
                .instructor("Instructor Name")
                .thumbnailPath("/path/to/thumbnail")
                .grade(A)
                .ncsClassification(BUSINESS_MANAGEMENT)
                .setDuration(30)
                .fundingType(REFUNDABLE)
                .cardType(List.of(NATIONAL_EMPLOYMENT_SUPPORT_CARD, CORPORATE_TRAINING_SUPPORT_CARD))
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        Course storedCourse = coursesTable.getItem(Key.builder()
                .partitionValue(courseId)
                .build());

        assertNotNull(storedCourse, "Course should be saved in DynamoDB");
        assertEquals(courseId, storedCourse.getCourseId(), "Course ID should match");
        assertEquals("Course Title", storedCourse.getCourseTitle(), "Course Title should match");
        assertEquals("Course Introduction", storedCourse.getCourseIntroduce(), "Course Introduction should match");
        assertEquals(ACTIVE, storedCourse.getStatus(), "Status should be ACTIVE");
        assertEquals(LocalDate.of(2025, 1, 1), storedCourse.getActiveStartDate(), "Start Date should match");
        assertEquals(LocalDate.of(2025, 12, 31), storedCourse.getActiveEndDate(), "End Date should match");
        assertEquals("Instructor Name", storedCourse.getInstructor(), "Instructor should match");
        assertEquals("/path/to/thumbnail", storedCourse.getThumbnailPath(), "Thumbnail path should match");
        assertEquals(A, storedCourse.getGrade(), "Grade should match");
        assertEquals(BUSINESS_MANAGEMENT, storedCourse.getNcsClassification(), "Category should match");
        assertEquals(30, storedCourse.getSetDuration(), "Duration should match");
        assertEquals(REFUNDABLE, storedCourse.getFundingType(), "Funding Type should match");
        assertEquals(List.of(NATIONAL_EMPLOYMENT_SUPPORT_CARD, CORPORATE_TRAINING_SUPPORT_CARD), storedCourse.getCardType(), "Card Types should match");


    }

    @Test
    @DisplayName("setDuration이 음수일 경우 400 Bad Request로 응답한다.")
    void createCourse_shouldReturnBadRequest_whenDurationIsNegative() throws Exception {
        // given
        AdminCourseCreateRequestDto requestDto = AdminCourseCreateRequestDto.builder()
                .courseTitle("Course Title")
                .courseIntroduce("Course Introduction")
                .status(ACTIVE)
                .activeStartDate(LocalDate.of(2025, 1, 1))
                .activeEndDate(LocalDate.of(2025, 12, 31))
                .instructor("Instructor Name")
                .thumbnailPath("/path/to/thumbnail")
                .grade(A)
                .ncsClassification(BUSINESS_MANAGEMENT)
                .setDuration(-1)
                .fundingType(REFUNDABLE)
                .cardType(List.of(NATIONAL_EMPLOYMENT_SUPPORT_CARD, CORPORATE_TRAINING_SUPPORT_CARD))
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("훈련과정을 수정하면 DynamoDb에서 수정된 데이터가 반영된다.")
    void updateCourse_shouldUpdateCourseData() throws Exception {
        // given
        courseId = "course_" + UUID.randomUUID();
        Course existingCourse = Course.builder()
                .courseId(courseId)
                .courseTitle("Original Title")
                .courseIntroduce("Original Introduction")
                .status(ACTIVE)
                .activeStartDate(LocalDate.of(2025, 1, 1))
                .activeEndDate(LocalDate.of(2025, 12, 31))
                .instructor("Original Instructor")
                .thumbnailPath("/original/path/to/thumbnail")
                .grade(A)
                .ncsClassification(BUSINESS_MANAGEMENT)
                .setDuration(30)
                .fundingType(REFUNDABLE)
                .cardType(List.of(NATIONAL_EMPLOYMENT_SUPPORT_CARD))
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();

        coursesTable.putItem(existingCourse);

        AdminUpdateCourseRequestDto requestDto = AdminUpdateCourseRequestDto.builder()
                .courseId(courseId)
                .courseTitle("Updated Title")
                .courseIntroduce("Updated Introduction")
                .status(null)
                .activeStartDate(LocalDate.of(2025, 6, 1))
                .activeEndDate(null)
                .instructor("Updated Instructor")
                .thumbnailPath("/updated/path/to/thumbnail")
                .grade(null)
                .ncsClassification(null)
                .setDuration(60)
                .fundingType(null)
                .cardType(List.of(CORPORATE_TRAINING_SUPPORT_CARD))
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when
        mockMvc.perform(put("/api/v1/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Course updated successfully."));

        // then
        Course updatedCourse = coursesTable.getItem(Key.builder()
                .partitionValue(courseId)
                .build());

        assertNotNull(updatedCourse, "Updated course should exist in DynamoDB");
        assertEquals("Updated Title", updatedCourse.getCourseTitle(), "Course Title should be updated");
        assertEquals("Updated Introduction", updatedCourse.getCourseIntroduce(), "Course Introduction should be updated");
        assertEquals(ACTIVE, updatedCourse.getStatus(), "Course status should remain unchanged");
        assertEquals(LocalDate.of(2025, 6, 1), updatedCourse.getActiveStartDate(), "Active start date should be updated");
        assertEquals(LocalDate.of(2025, 12, 31), updatedCourse.getActiveEndDate(), "Active end date should remain unchanged");
        assertEquals("Updated Instructor", updatedCourse.getInstructor(), "Instructor should be updated");
        assertEquals("/updated/path/to/thumbnail", updatedCourse.getThumbnailPath(), "Thumbnail path should be updated");
        assertEquals(A, updatedCourse.getGrade(), "Grade should remain unchanged");
        assertEquals(BUSINESS_MANAGEMENT, updatedCourse.getNcsClassification(), "NCS classification should remain unchanged");
        assertEquals(60, updatedCourse.getSetDuration(), "Duration should be updated");
        assertEquals(REFUNDABLE, updatedCourse.getFundingType(), "Funding type should remain unchanged");
        assertEquals(List.of(CORPORATE_TRAINING_SUPPORT_CARD), updatedCourse.getCardType(), "Card type should be updated");
    }

    @Test
    @DisplayName("Session 생성하면 DynamoDb로 저장하고 200 OK로 응답한다.")
    void createSession_shouldReturnSuccessMessage() throws Exception {
        // given
        courseId = "course_1234";
        sessionId = "session_" + UUID.randomUUID();
        String sessionTitle = "Introduction to Java";
        int sessionIndex = 1;

        AdminSessionCreateRequestDto requestDto = AdminSessionCreateRequestDto.builder()
                .courseId(courseId)
                .sessionId(sessionId)
                .sessionTitle(sessionTitle)
                .sessionIndex(sessionIndex)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/admin/courses/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();

        Session storedSession = sessionsTable.getItem(Key.builder()
                .partitionValue(courseId)
                .sortValue(sessionId)
                .build());

        assertNotNull(storedSession);
        assertEquals(sessionTitle, storedSession.getSessionTitle());
        assertEquals(sessionIndex, storedSession.getSessionIndex());
    }

    @Test
    @DisplayName("Content 생성하면 DynamoDb로 저장하고 200 OK로 응답한다.")
    void createContent_shouldReturnSuccessMessage() throws Exception {
        // given
        courseId = "course_1234";
        sessionId = "session_5678";
        contentId = "content_" + UUID.randomUUID();

        AdminContentCreateRequestDto requestDto = AdminContentCreateRequestDto.builder()
                .courseId(courseId)
                .sessionId(sessionId)
                .contentId(contentId)
                .contentTitle("Introduction to Java")
                .contentType(VIDEO)
                .contentIndex(1)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/admin/courses/sessions/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        Content storedContent = contentsTable.getItem(Key.builder()
                .partitionValue(courseId)
                .sortValue(contentId)
                .build());

        assertNotNull(storedContent, "Content should be saved in DynamoDB");
        assertEquals(courseId, storedContent.getCourseId(), "Course ID should match");
        assertEquals(sessionId, storedContent.getSessionId(), "Session ID should match");
        assertEquals(contentId, storedContent.getContentId(), "Content ID should match");
        assertEquals("Introduction to Java", storedContent.getContentTitle(), "Content Title should match");
        assertEquals("VIDEO", storedContent.getContentType().name(), "Content Type should match");
        assertEquals(1, storedContent.getContentIndex(), "Content Index should match");
    }

    @Test
    @DisplayName("Content Title이 비어있을 경우 400 Bad Request로 응답한다.")
    void createContent_shouldReturnBadRequest_whenContentTitleIsBlank() throws Exception {
        // given
        AdminContentCreateRequestDto requestDto = AdminContentCreateRequestDto.builder()
                .courseId("course_1234")
                .sessionId("session_5678")
                .contentTitle("")
                .contentType(VIDEO)
                .contentIndex(1)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/admin/courses/sessions/contents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("여러 Quiz를 생성하면 DynamoDB에 저장하고 200 OK로 응답한다.")
    void createQuiz_shouldReturnSuccessMessage() throws Exception {
        // given
        courseId = "course_1234";
        contentId = "content_5678";
        String quizId_1 = "quiz_" + UUID.randomUUID();
        String quizId_2 = "quiz_" + UUID.randomUUID();

        List<QuizDto> quizDtos = List.of(
                QuizDto.builder()
                        .quizId(quizId_1)
                        .quizIndex(1)
                        .question("What is the capital of France?")
                        .options(List.of("Paris", "London", "Berlin", "Madrid"))
                        .answer(0)
                        .explanation("Paris is the capital city of France.")
                        .build(),
                QuizDto.builder()
                        .quizId(quizId_2)
                        .quizIndex(2)
                        .question("What is the capital of Germany?")
                        .options(List.of("Berlin", "London", "Paris", "Madrid"))
                        .answer(0)
                        .explanation("Berlin is the capital city of Germany.")
                        .build()
        );

        AdminCreateQuizRequestDto requestDto = AdminCreateQuizRequestDto.builder()
                .courseId(courseId)
                .contentId(contentId)
                .quizzes(quizDtos)
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(put("/api/v1/admin/courses/sessions/contents/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizzes", hasSize(2)))
                .andExpect(jsonPath("$.quizzes[0]").isString())
                .andExpect(jsonPath("$.quizzes[1]").isString());

        for (QuizDto quizDto : quizDtos) {
            String quizId = quizDto.getQuizId();

            Quiz storedQuiz = quizzesTable.getItem(Key.builder()
                    .partitionValue(courseId)
                    .sortValue(quizId)
                    .build());

            quizIds.add(quizId);

            assertNotNull(storedQuiz, "Quiz should be saved in DynamoDB");
            assertEquals(courseId, storedQuiz.getCourseId(), "Course ID should match");
            assertEquals(contentId, storedQuiz.getContentId(), "Content ID should match");
            assertEquals(quizId, storedQuiz.getQuizId(), "Quiz ID should match");
            assertEquals(quizDto.getQuizIndex(), storedQuiz.getQuizIndex(), "Quiz Index should match");
            assertEquals(quizDto.getQuestion(), storedQuiz.getQuestion(), "Question should match");
            assertEquals(quizDto.getOptions(), storedQuiz.getOptions(), "Options should match");
            assertEquals(quizDto.getAnswer(), storedQuiz.getAnswer(), "Answer should match");
            assertEquals(quizDto.getExplanation(), storedQuiz.getExplanation(), "Explanation should match");
        }
    }

}