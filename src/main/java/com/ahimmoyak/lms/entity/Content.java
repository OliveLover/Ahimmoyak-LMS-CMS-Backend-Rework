package com.ahimmoyak.lms.entity;

import com.ahimmoyak.lms.dto.course.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Content {

    public static final TableSchema<Content> CONTENTS_TABLE_SCHEMA = TableSchema.fromClass(Content.class);

    private String courseId;
    private String contentId;
    private String sessionId;
    private String contentTitle;
    private ContentType contentType;
    private int contentIndex;
    private String videoPath;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("course_id")
    public String getCourseId() {
        return courseId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("content_id")
    public String getContentId() {
        return contentId;
    }

    @DynamoDbAttribute("session_id")
    public String getSessionId() {
        return sessionId;
    }

    @DynamoDbAttribute("content_title")
    public String getContentTitle() {
        return contentTitle;
    }

    @DynamoDbAttribute("content_type")
    public ContentType getContentType() {
        return contentType;
    }

    @DynamoDbAttribute("content_index")
    public int getContentIndex() {
        return contentIndex;
    }

    @DynamoDbAttribute("video_path")
    public String getVideoPath() {
        return videoPath;
    }

}
