package com.api.edocgen.util;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@UtilityClass
public class AppUtils {

    public static ByteArrayResource getByteArrayResource(String input) {
//        Path tempFile = Files.createTempFile(null, null);
//        Files.write(tempFile, input.getBytes(StandardCharsets.UTF_8));
        return new ByteArrayResource(input.getBytes(StandardCharsets.UTF_8)) {
            @Override
            public String getFilename() {
                return UUID.randomUUID().toString();
            }
        };
//        return new ByteArrayResource(Files.readAllBytes(tempFile));
    }
    public static ByteArrayResource getByteArrayResource(byte[] input) {
        return new ByteArrayResource(input) {
            @Override
            public String getFilename() {
                return UUID.randomUUID().toString();
            }
        };
    }

}
