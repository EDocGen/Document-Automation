package com.api.edocgen.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.*;
@Data
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class OutputDto {
    String _id;
}
