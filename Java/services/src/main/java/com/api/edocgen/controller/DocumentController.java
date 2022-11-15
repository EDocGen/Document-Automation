package com.api.edocgen.controller;

import com.api.edocgen.dto.InputDto;
import com.api.edocgen.dto.ResponseDto;
import com.api.edocgen.service.GenerateDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RequestMapping("/document")
@RestController
@RequiredArgsConstructor
public class DocumentController {


    private final GenerateDocumentService documentService;

    /**
     * Accept the data as text/plain as we don't process the input data
     * It just needs to be handed over to the edocgen
     * @param input
     * @return
     */
    @PostMapping(value = "/{document_id}/{email}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseDto generateDocument(@PathVariable("document_id") String documentId,
                                        @PathVariable String email,
                                        @RequestParam(value = "is_bulk", required = false, defaultValue = "true") boolean isBulk,
                                        @RequestParam(value = "output_file_format", required = false, defaultValue = "pdf") String format,
                                        // Accept any type of data as string
                                        @RequestParam("file") MultipartFile file) {
        if(Objects.isNull(file)) {
            return new ResponseDto("Input is empty");
        }
        try {
            documentService.getDocByApiAsync(documentId, format, file, isBulk, email);
        }catch (Exception e) {
            return new ResponseDto("Your request has been failed. Please check your input", HttpStatus.BAD_REQUEST);
        }
        return new ResponseDto("Your request has been submitted. You will receive the email with document");
    }

}
