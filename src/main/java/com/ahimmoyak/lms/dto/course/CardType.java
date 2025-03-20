package com.ahimmoyak.lms.dto.course;

import lombok.Getter;

@Getter
public enum CardType {

    PENDING("미정"),
    NATIONAL_EMPLOYMENT_SUPPORT_CARD("내일배움카드"),
    CORPORATE_TRAINING_SUPPORT_CARD("기업직업훈련카드");

    private final String typeName;

    CardType(String typeName) {
        this.typeName = typeName;
    }

}
