package com.api.edocgen.util;

import com.api.edocgen.service.LoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCache {

    private static String CACHE_KEY = "x-access-token";

    private static PassiveExpiringMap<String, String> tokenCache;

    static {
        // token will be cached for 20 mins
        tokenCache = new PassiveExpiringMap<>(20 * 60 * 1000);
    }

    @Autowired
    private final LoginService loginService;

    public String getToken() {
        String accessToken = tokenCache.get(CACHE_KEY);
        if(StringUtils.isEmpty(accessToken)) {
            synchronized (this) {
                accessToken = loginService.login();
                tokenCache.put(CACHE_KEY,accessToken);
            }
        }
        return accessToken;
    }
}
