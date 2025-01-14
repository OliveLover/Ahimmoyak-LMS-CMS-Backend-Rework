package com.ahimmoyak.lms.entity;

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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Course {

    public static final TableSchema<Course> COURSES_TABLE_SCHEMA = TableSchema.fromClass(Course.class);

    private String courseId;
    private String courseTitle;
    private String courseIntroduce;
    private String status;
    private LocalDate activeStartDate;
    private LocalDate activeEndDate;
    private String instructor;
    private String thumbnailPath;
    private String grade;
    private String category;
    private int setDuration;
    private String fundingType;
    private List<String> cardType;
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
    public String getStatus() {
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

    @DynamoDbAttribute("grade")
    public String getGrade() {
        return grade;
    }

    @DynamoDbAttribute("category")
    public String getCategory() {
        return category;
    }

    @DynamoDbAttribute("set_duration")
    public int getSetDuration() {
        return setDuration;
    }

    @DynamoDbAttribute("funding_type")
    public String getFundingType() {
        return fundingType;
    }

    @DynamoDbAttribute("card_type")
    public List<String> getCardType() {
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
