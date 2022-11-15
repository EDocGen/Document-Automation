using Microsoft.Extensions.Configuration;
using Newtonsoft.Json;
using RestSharp;
using System;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using TemplateBasedApplication.Exceptions;
using TemplateBasedApplication.Models.Dto;

namespace TemplateBasedApplication.Services
{
    public class LoginService
    {
        private readonly string username;
        private readonly string password;

        public LoginService(IConfiguration configuration)
        {
            this.username = configuration.GetSection("UserInfo:Username").Value;
            this.password = configuration.GetSection("UserInfo:Password").Value;
        }
        public async Task<string> loginAccount()
        {
            var client = new RestClient("https://app.edocgen.com");
            var request = new RestRequest("/login", Method.Post);
            
            request.AddHeader("Content-Type", "application/json");
            request.AddJsonBody(
                new
                {
                    username = username,
                    password = password
                });

            RestResponse response;
            try
            {
                response = await client.ExecuteAsync(request);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                throw new LoginException($"Error occured while getting token with username: {username}");
            }
            
            validateResponse(response);
            return JsonConvert.DeserializeObject<AuthenticationDto>(response.Content).token;           
        }
        private void validateResponse(RestResponse response)
        {
            if (isNotResponseValid(response))
                throw new InvalidServiceResponseException("Error occurred while generating document");
        }

        private bool isNotResponseValid(RestResponse response)
        {
            return !(response != null && response.StatusCode == System.Net.HttpStatusCode.OK);
        }

    }
}
