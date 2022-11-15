package com.api.edocgen.service;

import com.api.edocgen.util.HeaderUtils;
import com.api.edocgen.util.TokenCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class EmailService {

    public static String urlOutputEmail = "https://app.edocgen.com/api/v1/output/email";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HeaderUtils headerUtils;

    public void sendOutputViaEmail(String outId, String emailId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("outId", outId);
            body.add("emailId", emailId);
            // set Headers
            HttpHeaders headers = headerUtils.buildHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
            // send the request to generate document
            HttpEntity requestEntity = new HttpEntity(body, headers);

            ResponseEntity<String> generateResponse = restTemplate.postForEntity(urlOutputEmail, requestEntity, String.class);

            log.info("Send to edocGen Response : " + generateResponse.getStatusCode());
            if (HttpStatus.OK == generateResponse.getStatusCode()) {
                log.info("Email sent");
            }
        } catch (Exception e) {
            log.error("Exception During Sending Email. Check if document id is valid", e);
        }
    }

}
