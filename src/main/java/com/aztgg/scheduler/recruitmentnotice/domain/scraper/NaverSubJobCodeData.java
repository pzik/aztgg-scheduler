package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

public enum NaverSubJobCodeData {
    FRONTEND("Software Development_FrontEnd", "1010001"),
    ANDROID("Software Development_Android", "1010002"),
    IOS("Software Development_iOS", "1010003"),
    BACKEND("Software Development_Backend", "1010004"),
    AIML("Software Development_AI/ML", "1010005"),
    DATAENGINEERING("Software Development_Data Engineering", "1010006"),
    EMBEDDEDSW("Software Development_Embedded SW", "1010007"),
    GRAPHICS("Software Development_Graphics", "1010008"),
    SWCOMMON("Software Development_SW 공통", "1010020"),

    HARDWARE("Hardware Development_Hardware", "1020001"),
    INFRAENGINEERING("Infra Engineering_Ingra Engineering", "1030001"),
    DATACENTERENGINEERING("Infra Engineering_Data Center Engineering", "1030002"),
    SECURITY("Security_Security", "1040001"),
    TECHSTAFF("Tech Operation_TechStaff", "1050001"),
    QA("Tech Operation_QA", "1050002"),
    TECHCOMMON("Tech_Common", "1060001"),

    PRODUCTDESIGN("Product Design_Product Design", "2010001"),
    DVCBD("Visual Comm. & Brand Design_Visual Comm. & Brand Design", "2020001"),
    INTERACTIVEDESIGN("Interactive Design_Interactive Design", "2030001"),
    PEIP("Experience Insight & Planning Experience Insight & Planning", "2050001"),
    CONTENTWRITING("Content Writing Content Writing", "2060001"),
    DESIGNCOMMON("Design_common", "2070001");

    private final String label; // 라벨 필드
    private final String code;  // 코드 필드

    NaverSubJobCodeData(String label, String code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public String getCode() {
        return code;
    }
}
