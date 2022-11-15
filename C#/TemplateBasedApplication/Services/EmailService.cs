using Newtonsoft.Json;
using RestSharp;
using System;
using System.Linq;
using System.Threading;
using System.Threading.Tasks;
using TemplateBasedApplication.Exceptions;
using TemplateBasedApplication.Helper.Constants;
using TemplateBasedApplication.Models;
using TemplateBasedApplication.Models.Dto;

namespace TemplateBasedApplication.Services
{
    public class EmailService
    {
        private CacheService cacheService;
        public EmailService(CacheService cacheService)
        {
            this.cacheService = cacheService;
        }

        public async Task processOutput(FileRequestModel fileRequestModel, string email)
        {
            try
            {
                var outputId = getOutputId(fileRequestModel);

                var client = new RestClient("https://app.edocgen.com");
                var request = new RestRequest("/api/v1/output/email", Method.Post);
                request.AddHeader("Content-Type", "application/json");


                request.AddHeader("x-access-token", fileRequestModel.token);

                request.AddJsonBody(
                                new
                                {
                                    outId = outputId,
                                    emailId = email
                                });
                var response = await client.ExecuteAsync(request);
                validateResponse(response);
            }
            catch (Exception ex)
            {
                throw new ProcessOutputException(ex.Message);
            }  
        }

        private string getOutputId(FileRequestModel fileRequestModel)
        {
            Thread.Sleep(10000);
            var client = new RestClient("https://app.edocgen.com");
            var request = new RestRequest($"/api/v1/output/name/{fileRequestModel.fileName}.{FileGenerationConstants.PDF_OUTPUT_FORMAT}.{FileGenerationConstants.ZIP_OUTPUT_FORMAT}", Method.Get);
            request.AddHeader("Content-Type", "multipart/form-data");
            request.AddHeader("x-access-token", fileRequestModel.token);

            var response = client.Execute(request);
            validateResponse(response);

            var outputModelDto = JsonConvert.DeserializeObject<OutputModelDto>(response.Content);
            validateOutputResponse(outputModelDto);
            return outputModelDto.output[0]._id;
        }

        private void validateResponse(RestResponse response)
        {
            if (isNotResponseValid(response))
                throw new InvalidServiceResponseException($"Error occurred as a result of service call: {response.ResponseUri} , " +
                    $"message: {response.ErrorMessage} , " +
                    $"exception: {response.ErrorException}");
        }

        private bool isNotResponseValid(RestResponse response)
        {
            return !(response != null && response.StatusCode == System.Net.HttpStatusCode.OK);
        }

        private void validateOutputResponse(OutputModelDto outputModelDto)
        {
            if (isNotOutputResponseValid(outputModelDto))
                throw new InvalidOutputResponseException("Output model is not valid");
        }

        private bool isNotOutputResponseValid(OutputModelDto outputModelDto)
        {
            return !(outputModelDto != null && outputModelDto.output.Any());
        }
    }
}
