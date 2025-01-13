package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.CourseCreateRequestDto;
import com.ahimmoyak.lms.entity.Course;
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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
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

    private final DynamoDbTable<Course> courseTable;

    private final String testPk = "course_123";

    @Autowired
    public CourseServiceTest(DynamoDbEnhancedClient enhancedClient) {
        this.courseTable = enhancedClient.table("courses", Course.COURSE_TABLE_SCHEMA);
    }

    @AfterEach
    void deleteCourseData() {
        courseTable.deleteItem(builder -> builder.key(k -> k.partitionValue(testPk)));
    }

    @Test
    @DisplayName("훈련과정 생성하면 200 OK로 응답한다.")
    void createCourse_shouldReturnSuccessMessage() throws Exception {
        // given
        CourseCreateRequestDto requestDto = CourseCreateRequestDto.builder()
                .courseId(testPk)
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
                .courseId("course_123")
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

}