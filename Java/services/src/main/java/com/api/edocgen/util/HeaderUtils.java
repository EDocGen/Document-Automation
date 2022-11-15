package com.api.edocgen.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class HeaderUtils {

    @Autowired
    private TokenCache tokenCache;

    public HttpHeaders buildHeaders() {
        return HeaderBuilder
                .builder()
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accessToken(tokenCache.getToken())
                .build();
    }

}
