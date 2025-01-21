package com.ahimmoyak.lms.entity;

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
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Session {

    public static final TableSchema<Session> SESSIONS_TABLE_SCHEMA = TableSchema.fromClass(Session.class);

    private String courseId;
    private String sessionId;
    private String sessionTitle;
    private int sessionIndex;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("course_id")
    public String getCourseId() {
        return courseId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("session_id")
    public String getSessionId() {
        return sessionId;
    }

    @DynamoDbAttribute("session_title")
    public String getSessionTitle() {
        return sessionTitle;
    }

    @DynamoDbAttribute("session_index")
    public int getSessionIndex() {
        return sessionIndex;
    }

}
