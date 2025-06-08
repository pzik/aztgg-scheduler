package com.aztgg.scheduler.recruitmentnotice.domain.categoryclassifier;

import com.aztgg.scheduler.global.asset.PredefinedStandardCategory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class CategoryClassifierSystemInstruction {

    private final ObjectMapper objectMapper;
    private final PredefinedStandardCategory[] standardCategories;

    public CategoryClassifierSystemInstruction(ObjectMapper objectMapper, PredefinedStandardCategory[] standardCategories) {
        this.objectMapper = objectMapper;
        this.standardCategories = standardCategories;
    }

    public String print() {
        String overview = """
                You are the world's leading AI category classifier that categorizes unstandardized job names in company-specific job postings into standardized categories.
                The user delivers a message** in the form of **JSON, including the title of the **employment notice, company code, identifier, and the company's job name**.
                Your main goal is to analyze the JSON data **and convert it into a standardized category corresponding to '<standard-category>' for each identifier, and output the result in the JSON format **. You must respond **only in the JSON format.
                **Never use Internet browsing when you're working on classification.
                                
                Refer to '<example>' for the format of the input and output JSON.
                """;

        String example = """
                <example>
                - **The size** of the input JSON array and the size** of the output JSON array must be the same,
                **Results must be** included for all 'recruitmentNoticeId'; there must be no missing items.
                 
                <input>
                [
                	{
                		"recruitmentNoticeId": 1,
                		"jobOfferTitle": "iOS Platform Engineer",
                		"companyCode": "TOSS",
                		"categories": ["Engineering (Platform)"]
                	},
                	{
                		"recruitmentNoticeId": 2,
                		"jobOfferTitle": "[Tech] 셀러프로덕트실 메뉴프로덕트팀 백엔드 개발자",
                		"companyCode": "WOOWAHAN",
                		"categories": ["서버/백엔드"]
                	},
                	{
                		"recruitmentNoticeId": 3,
                		"jobOfferTitle": "Software Engineer, Backend - 로컬 비즈니스 (Kotlin)",
                		"companyCode": "DANNGN",
                		"categories": ["Software Engineer, Backend"]
                	},
                	{
                		"recruitmentNoticeId": 4,
                		"jobOfferTitle": "백엔드 개발자 채용",
                		"companyCode": "NAVER",
                		"categories": []
                	}
                ]
                </input>
                <output>
                [
                	{
                		"recruitmentNoticeId": 1,
                		"standardCategory": "IOS",
                		"accuracy": 0.5
                	},
                	{
                		"recruitmentNoticeId": 2,
                		"standardCategory": "BACKEND",
                		"accuracy": 0.5
                	},
                	{
                		"recruitmentNoticeId": 3,
                		"standardCategory": "BACKEND",
                		"accuracy": 0.5
                	},
                	{
                		"recruitmentNoticeId": 4,
                		"standardCategory": "BACKEND",
                		"accuracy": 0.5
                	}
                ]
                - Among the output items, the 'accuracy' field is,
                Based on the given input, when converted to a standard category corresponding to '<standard-category>'
                **The accuracy you judge must be entered in double format** of 0 or more and 1 or less.
                                
                </output>
                </example>
                """;


        List<StandardCategoryClassifyFormat> formats = new ArrayList<>();
        for (var value : standardCategories) {
            formats.add(new StandardCategoryClassifyFormat(value.name(), value.getKeyword()));
        }
        String strStandardCategories;
        try {
            strStandardCategories = objectMapper.writeValueAsString(formats);
        } catch (Exception e) {
            throw new RuntimeException("internal server error", e.getCause());
        }
        String standardCategory = String.format("""
                <standard-category>
                - The 'standard category' is a criterion for classifying each job into a standardized category**.
                - Each item consists of three fields:
                    - 'standardCategory': Standard Category Name
                    - 'keyword': list of **keywords associated with the category** (comma-separated string)
                    - 'tip': Tip to help classify categories. the value may be null.
                - When performing the classification, you should compare the given job name with the 'keyword' and select the most appropriate 'standardCategory' by referring to the 'tip'.
                - **If it does not clearly fall under any category**, 'standardCategory' should be designated as 'ETC', in which case 'keyword' is 'null'.
                           
                %s
                </standard-category>
                """, strStandardCategories);

        return overview + "\n" + example + "\n" + standardCategory;
    }
}
