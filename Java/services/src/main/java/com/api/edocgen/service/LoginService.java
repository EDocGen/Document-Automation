package com.api.edocgen.service;

import com.api.edocgen.dto.LoginResponse;
import com.api.edocgen.exception.LoginException;
import com.api.edocgen.util.HeaderBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class LoginService {

    @Autowired
    private RestTemplate restTemplate;

    public static String urlLogin = "https://app.edocgen.com/login";
    public static String bodyLogin = "{ \"username\": \"support@edocgen.com\", \"password\": \"SharadUpwork\"}";

    @PostConstruct
    public String login() {

        HttpHeaders headers = HeaderBuilder
                                .builder()
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .build();

        HttpEntity<String> requestEntity = new HttpEntity<>(bodyLogin, headers);
        String token;
        try {
            ResponseEntity<LoginResponse> responseEntity = restTemplate.exchange(urlLogin, HttpMethod.POST, requestEntity, LoginResponse.class);
            token = responseEntity.getBody().getToken();
        } catch (Exception e) {
            log.error("Failed to get the access token.", e);
            throw new LoginException("Failed to get the access token. Please check your username and password.");
        }
        return token;
    }
}
