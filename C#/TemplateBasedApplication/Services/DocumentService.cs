using RestSharp;
using System;
using System.Threading.Tasks;
using TemplateBasedApplication.Exceptions;
using TemplateBasedApplication.Helper.Constants;
using TemplateBasedApplication.Helper.FileUpload;
using TemplateBasedApplication.Models;
using TemplateBasedApplication.Models.Dto;

namespace TemplateBasedApplication.Services
{
    public class DocumentService
    {
        private readonly CacheService cacheService;
        private readonly EmailService emailService;
        private readonly FileUploadService fileUploadService;
        private readonly AuthenticationService authenticationService;

        public DocumentService(CacheService cacheService, EmailService emailService, 
            FileUploadService fileUploadService, AuthenticationService authenticationService)
        {
            this.cacheService = cacheService;
            this.emailService = emailService;
            this.fileUploadService = fileUploadService;
            this.authenticationService = authenticationService;
        }

        public async Task executeDocumentProcess(FileUploadDto fileUploadDto, string email)
        {
            try
            {
                FileUpload.validateFile(fileUploadDto);
                fileUploadService.saveFileToFolder(fileUploadDto);
                FileRequestModel fileRequestModel = createFileRequestModel(fileUploadDto);
                var response = await generateDocument(fileRequestModel);
                await emailService.processOutput(fileRequestModel, email);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                throw new FileGenerationException("Error occurred while generating document");
            }
        }

        private async void setServiceCredential()
        {
           await authenticationService.setCredential();
        }

        private FileRequestModel createFileRequestModel(FileUploadDto fileUploadDto)
        {
            var token = cacheService.Get<string>(RedisConstant.PDF_GENERATOR_TOKEN);
            var filePath = @"./Sources/" + fileUploadDto.file.FileName;
            var fileName = fileUploadDto.file.FileName + DateTime.Now.ToString();

            FileRequestModel fileRequestModel = new FileRequestModel()
            {
                token = token,
                filePath = filePath,
                fileName = fileName
            };

            return fileRequestModel;
        }

        private bool isNotResponseValid(RestResponse response) {
            return !(response != null && response.StatusCode == System.Net.HttpStatusCode.OK);
        }

        private async Task<RestResponse> generateDocument(FileRequestModel fileRequestModel)
        {
            var client = new RestClient("https://app.edocgen.com");
            var request = new RestRequest("/api/v1/document/generate/bulk", Method.Post);
            request.AddHeader("Content-Type", "multipart/form-data");
            request.AddHeader("x-access-token", fileRequestModel.token);
            request.AddParameter("documentId", FileGenerationConstants.DEFAULT_TEMPLATE_ID);
            request.AddFile("inputFile", fileRequestModel.filePath);
            request.AddParameter("outputFileName", fileRequestModel.fileName);
            request.AddParameter("format", FileGenerationConstants.PDF_OUTPUT_FORMAT);

            var response = await client.ExecuteAsync(request);

            validateResponse(response);
            return response;
        }

        private void validateResponse(RestResponse response)
        {
            if (isNotResponseValid(response))
                throw new InvalidServiceResponseException($"Error occurred as a result of service call: {response.ResponseUri} , " +
                    $"message: {response.ErrorMessage} , " +
                    $"exception: {response.ErrorException}");
        }        
    }
}
