package com.api.edocgen.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Objects;

public class HeaderBuilder {

    private MediaType accept;
    private MediaType contentType;
    private String token;
    private final HttpHeaders headers = new HttpHeaders();
    private static final String ACCESS_TOKEN = "x-access-token";

    public static HeaderBuilder builder() {
        return new HeaderBuilder();
    }

    public HeaderBuilder accept(MediaType accept) {
        this.accept = accept;
        return this;
    }

    public HeaderBuilder contentType(MediaType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HeaderBuilder accessToken(String token) {
        this.token = token;
        return this;
    }

    public HttpHeaders build() {
        if(Objects.nonNull(accept))
            headers.setAccept(Collections.singletonList(accept));
        if(Objects.nonNull(contentType))
            headers.setContentType(contentType);
        if(Objects.nonNull(token))
            headers.add(ACCESS_TOKEN, token);
        return headers;
    }

}
