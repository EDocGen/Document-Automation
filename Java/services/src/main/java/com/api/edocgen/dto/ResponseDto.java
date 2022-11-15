package com.api.edocgen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto {

    private Object result;
    private HttpStatus httpStatus;

    public ResponseDto(Object result) {
        this.result = result;
        this.httpStatus = HttpStatus.OK;
    }

}
