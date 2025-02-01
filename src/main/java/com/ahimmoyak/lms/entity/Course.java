package com.ahimmoyak.lms.entity;

import com.ahimmoyak.lms.dto.course.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.util.List;

@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Course {

    public static final TableSchema<Course> COURSES_TABLE_SCHEMA = TableSchema.fromClass(Course.class);

    private String courseId;
    private String courseTitle;
    private String courseIntroduce;
    private CourseStatus status;
    private LocalDate activeStartDate;
    private LocalDate activeEndDate;
    private String instructor;
    private String thumbnailPath;
    private String thumbnailId;
    private long thumbnailSize;
    private String thumbnailName;
    private CourseGrade grade;
    private NcsClassification ncsClassification;
    private int setDuration;
    private FundingType fundingType;
    private List<CardType> cardType;
    private LocalDate createdDate;
    private LocalDate modifiedDate;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("course_id")
    public String getCourseId() {
        return courseId;
    }

    @DynamoDbAttribute("course_title")
    public String getCourseTitle() {
        return courseTitle;
    }

    @DynamoDbAttribute("course_introduce")
    public String getCourseIntroduce() {
        return courseIntroduce;
    }

    @DynamoDbAttribute("status")
    public CourseStatus getStatus() {
        return status;
    }

    @DynamoDbAttribute("active_start_date")
    public LocalDate getActiveStartDate() {
        return activeStartDate;
    }

    @DynamoDbAttribute("active_end_date")
    public LocalDate getActiveEndDate() {
        return activeEndDate;
    }

    @DynamoDbAttribute("instructor")
    public String getInstructor() {
        return instructor;
    }

    @DynamoDbAttribute("thumbnail_path")
    public String getThumbnailPath() {
        return thumbnailPath;
    }

    @DynamoDbAttribute("thumbnail_id")
    public String getThumbnailId() {
        return thumbnailId;
    }

    @DynamoDbAttribute("thumbnail_size")
    public long getThumbnailSize() {
        return thumbnailSize;
    }

    @DynamoDbAttribute("thumbnail_name")
    public String getThumbnailName() {
        return thumbnailName;
    }

    @DynamoDbAttribute("grade")
    public CourseGrade getGrade() {
        return grade;
    }

    @DynamoDbAttribute("ncs_classification")
    public NcsClassification getNcsClassification() {
        return ncsClassification;
    }

    @DynamoDbAttribute("set_duration")
    public int getSetDuration() {
        return setDuration;
    }

    @DynamoDbAttribute("funding_type")
    public FundingType getFundingType() {
        return fundingType;
    }

    @DynamoDbAttribute("card_type")
    public List<CardType> getCardType() {
        return cardType;
    }

    @DynamoDbAttribute("created_date")
    public LocalDate getCreatedDate() {
        return createdDate;
    }

    @DynamoDbAttribute("modified_date")
    public LocalDate getModifiedDate() {
        return modifiedDate;
    }

}
