package com.api.edocgen.service;

import com.api.edocgen.dto.DocumentResponse;
import com.api.edocgen.util.HeaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HeaderUtils headerUtils;

    public static String urlQueryDocument = "https://app.edocgen.com/api/v1/document/?search_column=filename&search_value=";

    public String getTemplateId(String TemplateFileName) {

        String resourceUrl = urlQueryDocument + TemplateFileName;
        HttpHeaders headers = headerUtils.buildHeaders();
        HttpEntity request = new HttpEntity(headers);

        // Fetch JSON response as String wrapped in ResponseEntity
        ResponseEntity<DocumentResponse> response = restTemplate.exchange(resourceUrl, HttpMethod.GET, request, DocumentResponse.class);
        String templateId = response.getBody().getDocuments().get(0).get_id();
        log.error("Template id is : " + templateId);
        return templateId;
    }

}
