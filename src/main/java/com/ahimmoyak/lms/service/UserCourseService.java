package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.course.CardType;
import com.ahimmoyak.lms.dto.course.CoursesDto;
import com.ahimmoyak.lms.dto.course.user.UserMainNcsCoursesResponseDto;
import com.ahimmoyak.lms.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ahimmoyak.lms.dto.course.FundingType.PENDING;
import static com.ahimmoyak.lms.entity.Course.COURSES_TABLE_SCHEMA;
import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.ACTIVE;

@Service
public class UserCourseService {

    private final DynamoDbTable<Course> coursesTable;

    @Autowired
    public UserCourseService(DynamoDbEnhancedClient enhancedClient) {
        this.coursesTable = enhancedClient.table("courses", COURSES_TABLE_SCHEMA);
    }

    public ResponseEntity<UserMainNcsCoursesResponseDto> getActiveNcsCourses() {
        ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(Expression.builder()
                        .expression("#stat = :statusValue")
                        .expressionValues(Map.of(":statusValue", AttributeValue.builder().s(ACTIVE.toUpperCase()).build()))
                        .expressionNames(Map.of("#stat", "status"))
                        .build())
                .build();

        SdkIterable<Page<Course>> result = coursesTable.scan(enhancedRequest);

        List<Course> courses = new ArrayList<>(result.stream()
                .flatMap(page -> page.items().stream())
                .toList());

        Collections.shuffle(courses);

        List<CoursesDto> randomCourses = courses.stream()
                .limit(5)
                .map(course -> CoursesDto.builder()
                        .courseTitle(course.getCourseTitle())
                        .thumbnailPath(course.getThumbnailPath())
                        .ncsName(course.getNcsClassification().getDisplayName())
                        .fundingTypeName(course.getFundingType().getTypeName().equals(PENDING.getTypeName()) ? null : course.getFundingType().getTypeName())
                        .cardTypeNames(
                                course.getCardType().stream()
                                        .map(CardType::getTypeName)
                                        .toList()
                        )
                        .build())
                .toList();

        UserMainNcsCoursesResponseDto responseDto = UserMainNcsCoursesResponseDto.builder()
                .courses(randomCourses)
                .build();

        return ResponseEntity.ok(responseDto);
    }

}
