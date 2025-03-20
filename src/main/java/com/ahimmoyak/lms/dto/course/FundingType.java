package com.ahimmoyak.lms.dto.course;

import lombok.Getter;

@Getter
public enum FundingType {

    PENDING("미정"),
    REFUNDABLE("환급 과정"),
    NON_REFUNDABLE("비환급 과정");

    private final String typeName;

    FundingType(String typeName) {
        this.typeName = typeName;
    }

}
