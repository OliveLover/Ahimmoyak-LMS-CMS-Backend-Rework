package com.ahimmoyak.lms.dto.course;

import com.ahimmoyak.lms.dto.upload.FileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentDto {

    private String contentId;
    private int contentIndex;
    private String contentTitle;
    private ContentType contentType;
    private String videoPath;
    private String fileId;
    private FileType fileType;
    private int videoDuration;
    private long fileSize;
    private List<QuizDto> quizzes;

}
