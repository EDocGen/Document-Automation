package com.api.edocgen.dto;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.*;
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class DocumentResponse {
    String status;
    List<DocumentDto> documents; 
}
