package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.CourseCreateRequestDto;
import com.ahimmoyak.lms.dto.MessageResponseDto;
import com.ahimmoyak.lms.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.time.LocalDate;
import java.util.UUID;

import static com.ahimmoyak.lms.entity.Course.COURSE_TABLE_SCHEMA;

@Service
public class CourseService {

    private final DynamoDbTable<Course> courseTable;

    @Autowired
    public CourseService(DynamoDbEnhancedClient enhancedClient) {
        this.courseTable = enhancedClient.table("courses", COURSE_TABLE_SCHEMA);
    }

    public ResponseEntity<MessageResponseDto> createCourse(CourseCreateRequestDto requestDto) {
        String courseId = requestDto.getCourseId();

        if (requestDto.getCourseId() == null) {
            System.out.println(courseId);
            courseId = "course_" + UUID.randomUUID();
        }

        Course course = Course.builder()
                .courseId(courseId)
                .title(requestDto.getCourseTitle())
                .introduce(requestDto.getCourseIntroduce())
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
        courseTable.putItem(course);

        MessageResponseDto responseDto = new MessageResponseDto(
                "Course created successfully."
        );

        return ResponseEntity.ok(responseDto);
    }

}
