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

import java.util.List;

@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
public class Quiz {

    public static final TableSchema<Quiz> QUIZZES_TABLE_SCHEMA = TableSchema.fromClass(Quiz.class);

    private String courseId;
    private String quizId;
    private String contentId;
    private int quizIndex;
    private String question;
    private List<String> options;
    private Integer answer;
    private String explanation;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("course_id")
    public String getCourseId() {
        return courseId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("quiz_id")
    public String getQuizId() {
        return quizId;
    }

    @DynamoDbAttribute("content_id")
    public String getContentId() {
        return contentId;
    }

    @DynamoDbAttribute("quiz_index")
    public int getQuizIndex() {
        return quizIndex;
    }

    @DynamoDbAttribute("question")
    public String getQuestion() {
        return question;
    }

    @DynamoDbAttribute("options")
    public List<String> getOptions() {
        return options;
    }

    @DynamoDbAttribute("answer")
    public Integer getAnswer() {
        return answer;
    }

    @DynamoDbAttribute("explanation")
    public String getExplanation() {
        return explanation;
    }

}
