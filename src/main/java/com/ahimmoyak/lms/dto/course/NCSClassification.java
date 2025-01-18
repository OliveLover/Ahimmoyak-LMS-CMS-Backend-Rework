package com.ahimmoyak.lms.dto.course;

import lombok.Getter;

@Getter
public enum NCSClassification {
    BUSINESS_MANAGEMENT("01", "사업관리"),
    MANAGEMENT_ACCOUNTING_OFFICE_WORK("02", "경영·회계·사무"),
    FINANCE_INSURANCE("03", "금융·보험"),
    EDUCATION_NATURAL_SOCIAL_SCIENCES("04", "교육·자연·사회과학"),
    LAW_POLICE_FIRE_CORRECTIONS_NATIONAL_DEFENSE("05", "법률·경찰·소방·교도·국방"),
    HEALTH_MEDICAL_CARE("06", "보건·의료"),
    SOCIAL_WELFARE_RELIGION("07", "사회복지·종교"),
    CULTURE_ARTS_DESIGN_BROADCASTING("08", "문화·예술·디자인·방송"),
    DRIVING_TRANSPORTATION("09", "운전·운송"),
    SALES_MARKETING("10", "영업판매"),
    SECURITY_CLEANING("11", "경비·청소"),
    ACCOMMODATION_TRAVEL_LEISURE_SPORTS("12", "이용·숙박·여행·오락·스포츠"),
    FOOD_SERVICE("13", "음식서비스"),
    CONSTRUCTION("14", "건설"),
    MACHINERY("15", "기계"),
    MATERIALS("16", "재료"),
    CHEMISTRY_BIOTECHNOLOGY("17", "화학·바이오"),
    TEXTILES_APPAREL("18", "섬유·의복"),
    ELECTRICAL_ELECTRONICS("19", "전기·전자"),
    INFORMATION_COMMUNICATIONS("20", "정보통신"),
    FOOD_PROCESSING("21", "식품가공"),
    PRINTING_WOODWORKING_FURNITURE_CRAFTS("22", "인쇄·목재·가구·공예"),
    ENVIRONMENT_ENERGY_SAFETY("23", "환경·에너지·안전"),
    AGRICULTURE_FORESTRY_FISHERIES("24", "농림어업");

    private final String codeNum;
    private final String displayName;

    NCSClassification(String codeNum, String displayName) {
        this.codeNum = codeNum;
        this.displayName = displayName;
    }

}
