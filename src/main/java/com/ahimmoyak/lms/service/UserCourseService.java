package com.ahimmoyak.lms.service;

import com.ahimmoyak.lms.dto.course.CardType;
import com.ahimmoyak.lms.dto.course.CoursesDto;
import com.ahimmoyak.lms.dto.course.NcsClassification;
import com.ahimmoyak.lms.dto.course.user.SessionPreviewDto;
import com.ahimmoyak.lms.dto.course.user.UserCourseDetailsResponseDto;
import com.ahimmoyak.lms.dto.course.user.UserCoursesResponseDto;
import com.ahimmoyak.lms.entity.Course;
import com.ahimmoyak.lms.entity.Session;
import com.ahimmoyak.lms.exception.NotFoundException;
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
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

import static com.ahimmoyak.lms.dto.course.FundingType.PENDING;
import static com.ahimmoyak.lms.dto.course.NcsClassification.UNDEFINED;
import static com.ahimmoyak.lms.entity.Course.COURSES_TABLE_SCHEMA;
import static com.ahimmoyak.lms.entity.Session.SESSIONS_TABLE_SCHEMA;
import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.ACTIVE;

@Service
public class UserCourseService {

    private final DynamoDbTable<Course> coursesTable;
    private final DynamoDbTable<Session> sessionsTable;

    @Autowired
    public UserCourseService(DynamoDbEnhancedClient enhancedClient) {
        this.coursesTable = enhancedClient.table("courses", COURSES_TABLE_SCHEMA);
        this.sessionsTable = enhancedClient.table("sessions", SESSIONS_TABLE_SCHEMA);
    }

    public ResponseEntity<UserCoursesResponseDto> getActiveNcsCourses() {
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
                .filter(course -> course.getNcsClassification() != UNDEFINED)
                .limit(5)
                .map(course -> CoursesDto.builder()
                        .courseId(course.getCourseId())
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

        UserCoursesResponseDto responseDto = UserCoursesResponseDto.builder()
                .courses(randomCourses)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<UserCoursesResponseDto> getActiveCoursesByCode(String code) {
        List<CoursesDto> courses = new ArrayList<>();

        Optional<NcsClassification> ncsClassificationOptional = Arrays.stream(NcsClassification.values())
                .filter(c -> c.getCodeNum().equals(code))
                .findFirst();

        if (ncsClassificationOptional.isPresent()) {
            NcsClassification ncsClassification = ncsClassificationOptional.get();

            ScanEnhancedRequest enhancedRequest = ScanEnhancedRequest.builder()
                    .filterExpression(Expression.builder()
                            .expression("#stat = :statusValue AND #ncs_classification = :ncsClassification")
                            .expressionValues(Map.of(":statusValue", AttributeValue.builder().s(ACTIVE.toUpperCase()).build(),
                                    ":ncsClassification", AttributeValue.builder().s(ncsClassification.toString().toUpperCase()).build()))
                            .expressionNames(Map.of("#stat", "status", "#ncs_classification", "ncs_classification"))
                            .build())
                    .build();

            SdkIterable<Page<Course>> result = coursesTable.scan(enhancedRequest);

            courses = result.stream()
                    .flatMap(page -> page.items().stream())
                    .map(course -> CoursesDto.builder()
                            .courseId(course.getCourseId())
                            .courseTitle(course.getCourseTitle())
                            .thumbnailPath(course.getThumbnailPath())
                            .ncsName(course.getNcsClassification().getDisplayName())
                            .fundingTypeName(course.getFundingType().getTypeName().equals(PENDING.getTypeName()) ? null : course.getFundingType().getTypeName())
                            .cardTypeNames(
                                    course.getCardType().stream()
                                            .map(CardType::getTypeName)
                                            .toList()
                            )
                            .build()
                    )
                    .toList();
        }

        UserCoursesResponseDto responseDto = UserCoursesResponseDto.builder()
                .courses(courses)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<UserCourseDetailsResponseDto> getActiveCourseDetails(String courseId) {
        QueryEnhancedRequest queryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Course>> result = coursesTable.query(queryRequest);

        Course course = result.stream()
                .flatMap(page -> page.items().stream())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Course not found with ID: " + courseId));

        QueryEnhancedRequest sessionQueryRequest = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId)))
                .build();

        SdkIterable<Page<Session>> sessionResult = sessionsTable.query(sessionQueryRequest);

        List<SessionPreviewDto> sessionPreviews = sessionResult.stream()
                .flatMap(page -> page.items().stream())
                .map(session -> SessionPreviewDto.builder()
                        .sessionTitle(session.getSessionTitle())
                        .sessionIndex(session.getSessionIndex())
                        .build())
                .sorted(Comparator.comparingInt(SessionPreviewDto::getSessionIndex))
                .toList();

        UserCourseDetailsResponseDto responseDto = UserCourseDetailsResponseDto.builder()
                .courseId(course.getCourseId())
                .courseTitle(course.getCourseTitle())
                .courseIntroduce(course.getCourseIntroduce())
                .instructor(course.getInstructor())
                .thumbnailPath(course.getThumbnailPath())
                .startDate(course.getActiveStartDate())
                .endDate(course.getActiveEndDate())
                .setDuration(course.getSetDuration())
                .fundingTypeName(course.getFundingType().getTypeName().equals(PENDING.getTypeName()) ? null : course.getFundingType().getTypeName())
                .cardTypeNames(
                        course.getCardType().stream()
                                .map(CardType::getTypeName)
                                .toList()
                )
                .sessionPreviews(sessionPreviews)
                .build();

        return ResponseEntity.ok(responseDto);
    }
}
