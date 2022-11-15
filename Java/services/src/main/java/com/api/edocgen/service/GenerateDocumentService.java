package com.api.edocgen.service;

import com.api.edocgen.exception.FileNotGeneratedException;
import com.api.edocgen.util.AppUtils;
import com.api.edocgen.util.HeaderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.api.edocgen.dto.DBParameters;
import com.api.edocgen.dto.OutputDto;
import com.api.edocgen.dto.OutputResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class GenerateDocumentService {

    @Autowired
    private HeaderUtils headerUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EmailService emailService;

    /*
     * Standard URLs
     */
    public static String baseURL = "https://app.edocgen.com/api/v1/";
    public static String urlBulkGenerate = "https://app.edocgen.com/api/v1/document/generate/bulk";


    /*
     * User Configuration
     */
    public static String outputFilePath = "./";
    public static String sourcePath = "src/main/resources/";
    public static String pathFile = "my_json_data.json";
    public static String TemplateFileName = "edocgen_v1_union.docx";


    @PostConstruct
    public void postdata() throws Exception {

        /*
         *
         * Database Configiuration Settings
         */

        DBParameters dbParameters = new DBParameters();
        dbParameters.setDbLimit("100");
        dbParameters.setDbPassword("l8XuHH8Xax");
        dbParameters.setDbVendor("mysql");
        dbParameters.setDbUrl("jdbc:mysql://sql6508014@sql6.freesqldatabase.com:3306/sql6508014/sdtest");
        dbParameters.setDbQuery("select * from sdtest");


        /*
         * This program supports following methods:
         *
         *  ## Single File Generation API's ####
         *
         *  getDocByApi(String documentId, String outputFileName, String outputFormat)
         *
         *
         * ###### Bulk Files Generation API's  #####
         *
         * getDocsByApi (TemplateId, OutputFormat)                          ====> This method generate bulk document based on JSON template which is present locally and downloads .zip file
         * getDocsByDatabase(TemplateId , OutputFormat , DBConfiguration)   ====> This method generate bulk document from database entries which is hosted in mysql and downloads .zip file
         *
         * ## Generic Method works both for single and Bulk #####
         * getTemplateId(TemplateFileName);                                 ====> This method return templateid based on filename
         * sendOutputViaEmail(TemplateId , Email)                           ====> This method sends generated output files directly to email via given templateID.
         */

        // String templateID =  getTemplateId(TemplateFileName);
        //getDocsByApi("62d95a226760033416374c10", "pdf");
//        getDocsByDatabase("62bdaf8308bcc4761b6aa6b9", "pdf", dbParameters);
        // sendOutputViaEmail("62dfa645c2dae13e2ee6ec3e", "sdconnect18@protonmail.com");

        // getDocByApi("62bdaf8308bcc4761b6aa6b9", "SDTest", "pdf");

    }


    @Async
    public void getDocByApiAsync(String documentId,
                                 String fileFormat,
                                 MultipartFile input,
                                 boolean isBulk,
                                 String email
                                 ) {
        ByteArrayResource resource;
        try {
            byte[] bytes = input.getInputStream().readAllBytes();
            resource = new ByteArrayResource(bytes) {
                @Override
                public String getFilename() {
                    return pathFile;
                }
            };
            if(isBulk){
                getDocsInBulkMode(documentId,fileFormat, resource, email);
            } else {
                getDocByApi(documentId, fileFormat, resource, email);
            }
        } catch (Exception e) {
            log.error("Failed to generate the document", e);
        }
    }

    public void getDocByApi(String documentId, String outputFormat, ByteArrayResource resource, String email) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String outputFileName = UUID.randomUUID().toString();
        try {
            //create the body request
            MultiValueMap<String, Object> body = createBody(resource, documentId, outputFileName, outputFormat);

            //set Headers
            HttpHeaders headers = headerUtils.buildHeaders();
            //send the request to generate document
            HttpEntity requestEntity = new HttpEntity(body, headers);

            ResponseEntity<String> generateResponse = restTemplate.postForEntity(urlBulkGenerate,
                    requestEntity, String.class);
            if (HttpStatus.OK == generateResponse.getStatusCode()) {
                processOutput(outputFileName, outputFormat, true, email);
            }
        } catch (Exception e) {
            log.error("Error in the generating document", e);
        }
    }

    public void getDocsByDatabase(String documentId, String outputFormat, DBParameters dbparmeters, String email) throws Exception {

        String outputFileName = UUID.randomUUID().toString();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("documentId", documentId);
        body.add("format", outputFormat);
        body.add("outputFileName", outputFileName);
        body.add("dbVendor", dbparmeters.getDbVendor());
        body.add("dbUrl", dbparmeters.getDbUrl());
        body.add("dbLimit", dbparmeters.getDbLimit());
        body.add("dbPassword", dbparmeters.getDbPassword());
        body.add("dbQuery", dbparmeters.getDbQuery());

        // set Headers
        HttpHeaders headers = headerUtils.buildHeaders();

        // send the request to generate document
        HttpEntity requestEntity = new HttpEntity(body, headers);
        try {
            ResponseEntity<String> generateResponse = restTemplate.postForEntity(urlBulkGenerate, requestEntity, String.class);
            log.info("Send to edocGen Response : " + generateResponse.getStatusCode());
            if (HttpStatus.OK == generateResponse.getStatusCode()) {
                processOutput(outputFileName, outputFormat, false, email);
            }
        } catch (Exception e) {
            log.error("Failed to get the file downloaded");
        }

    }

    private void processOutput(String outputFileName, String outputFormat, boolean isSingleGeneration, String email) {
        HttpHeaders headers = headerUtils.buildHeaders();
        HttpEntity requestEntity = new HttpEntity(null, headers);

        ResponseEntity<OutputResultDto> result = restTemplate.exchange(baseURL + "output/name/" + outputFileName + "." + outputFormat, HttpMethod.GET, requestEntity, OutputResultDto.class);
        OutputDto responseOutput = null;

        if (!isSingleGeneration) {
            outputFormat = outputFormat + ".zip";
        }

        responseOutput = isFileGenerated(outputFileName, outputFormat, requestEntity, result);

        log.info("Output Document Id generated at edocgen is : " + responseOutput.get_id());
        String outputId = responseOutput.get_id();

        // output download
        try {
            emailService.sendOutputViaEmail(outputId, email);
            log.info("File has been sent over email for file : " + outputFileName + "." + outputFormat);
        } catch (Exception e) {
            log.error("Error while Sending the file over email");
            throw new FileNotGeneratedException("Error while Downloading File");
        }
    }

    private OutputDto isFileGenerated(String outputFileName, String outputFormat, HttpEntity requestEntity, ResponseEntity<OutputResultDto> result) {
        OutputDto responseOutput;
        int retryCounter = 0;
        try {
            while (result.getBody().getOutput().toString().length() <= 2 && retryCounter < 200) {
                log.info("Output file is still not available. Retrying again!!!! Counter : " + retryCounter);
                result = restTemplate.exchange(baseURL + "output/name/" + outputFileName + "." + outputFormat, HttpMethod.GET, requestEntity, OutputResultDto.class);
                retryCounter++;
                // spin lock for 2 secs
                Thread.sleep(5000);
            }
            responseOutput = result.getBody().getOutput().get(0);
        } catch (Exception error) {
            log.error("Error : Output file is not available after 200 tries");
            throw new FileNotGeneratedException("Error : Output file is not available after 200 tries");
        }
        return responseOutput;
    }

    public void getDocsInBulkMode(String documentId, String outputFormat, ByteArrayResource resource, String email ) throws Exception {

        String outputFileName = UUID.randomUUID().toString();
        try {
            // create the body request
            MultiValueMap<String, Object> body = createBody(resource, documentId, outputFileName, outputFormat);

            // set Headers
            HttpHeaders headers = headerUtils.buildHeaders();

            // send the request to generate document
            HttpEntity requestEntity = new HttpEntity(body, headers);
            ResponseEntity<String> generateResponse = restTemplate.postForEntity(urlBulkGenerate,
                    requestEntity, String.class);

            if (HttpStatus.OK == generateResponse.getStatusCode()) {
                log.info("Send to edocGen Response : " + generateResponse.getStatusCode());
                processOutput(outputFileName, outputFormat, false, email);
            } else {
                throw new HttpClientErrorException(generateResponse.getStatusCode());
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private MultiValueMap<String, Object> createBody(ByteArrayResource resource,
                                                     String documentId, String outputFileName, String outPutFormat) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("documentId", documentId);
//        try {
//            body.add("inputFile", getByteArrayResource());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        body.add("inputFile", resource);

        body.add("format", outPutFormat);
        body.add("outputFileName", outputFileName);
        // to download directly the file
        body.add("sync", true);
        return body;
    }

    private ByteArrayResource getByteArrayResource() throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream("/"+pathFile);

        byte[] bytes = resourceAsStream.readAllBytes();
        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return pathFile;
            }
        };
        return resource;
    }
}
