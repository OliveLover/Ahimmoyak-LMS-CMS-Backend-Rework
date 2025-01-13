package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.course.CourseCreateRequestDto;
import com.ahimmoyak.lms.dto.course.SessionCreateRequestDto;
import com.ahimmoyak.lms.entity.Course;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    private String courseId;

    private String sessionId;

    @Autowired
    public CourseServiceTest(DynamoDbEnhancedClient enhancedClient) {
        this.coursesTable = enhancedClient.table("courses", Course.COURSES_TABLE_SCHEMA);
        this.sessionsTable = enhancedClient.table("sessions", Session.SESSIONS_TABLE_SCHEMA);
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

    @Test
    @DisplayName("훈련과정 생성하면 DynamoDb로 저장하고 200 OK로 응답한다.")
    void createCourse_shouldReturnSuccessMessage() throws Exception {
        // given
        courseId = "coures_" + UUID.randomUUID();

        CourseCreateRequestDto requestDto = CourseCreateRequestDto.builder()
                .courseId(courseId)
                .courseTitle("Course Title")
                .courseIntroduce("Course Introduction")
                .status("ACTIVE")
                .activeStartDate(LocalDate.of(2025, 1, 1))
                .activeEndDate(LocalDate.of(2025, 12, 31))
                .instructor("Instructor Name")
                .thumbnailPath("/path/to/thumbnail")
                .grade("A")
                .category("Science")
                .setDuration(30)
                .fundingType("Government")
                .cardType(List.of("Type1", "Type2"))
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Course created successfully.")));

    }

    @Test
    @DisplayName("setDuration이 음수일 경우 400 Bad Request로 응답한다.")
    void createCourse_shouldReturnBadRequest_whenDurationIsNegative() throws Exception {
        // given
        CourseCreateRequestDto requestDto = CourseCreateRequestDto.builder()
                .courseTitle("Course Title")
                .courseIntroduce("Course Introduction")
                .status("ACTIVE")
                .activeStartDate(LocalDate.of(2025, 1, 1))
                .activeEndDate(LocalDate.of(2025, 12, 31))
                .instructor("Instructor Name")
                .thumbnailPath("/path/to/thumbnail")
                .grade("A")
                .category("Science")
                .setDuration(-1)
                .fundingType("Government")
                .cardType(List.of("Type1", "Type2"))
                .createdDate(LocalDate.now())
                .modifiedDate(LocalDate.now())
                .build();

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        // when & then
        mockMvc.perform(post("/api/v1/admin/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message[0]", is("The field 'setDuration' must be at least 0.")));
    }

    @Test
    @DisplayName("Session 생성하면 DynamoDb로 저장하고 200 OK로 응답한다.")
    void createSession_shouldReturnSuccessMessage() throws Exception {
        // given
        courseId = "course_1234";
        sessionId = "session_" + UUID.randomUUID();
        String sessionTitle = "Introduction to Java";
        int sessionIndex = 1;

        SessionCreateRequestDto requestDto = SessionCreateRequestDto.builder()
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
                .andExpect(jsonPath("$.message", is("Session created successfully.")))
                .andReturn();

        Session storedSession = sessionsTable.getItem(Key.builder()
                .partitionValue(courseId)
                .sortValue(sessionId)
                .build());

        assertNotNull(storedSession);
        assertEquals(sessionTitle, storedSession.getSessionTitle());
        assertEquals(sessionIndex, storedSession.getSessionIndex());
    }

}