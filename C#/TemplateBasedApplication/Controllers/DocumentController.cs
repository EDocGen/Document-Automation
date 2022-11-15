using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;
using TemplateBasedApplication.Services;
using TemplateBasedApplication.Models.Dto;
using System.IO;
using TemplateBasedApplication.Helper.Constants;

namespace TemplateBasedApplication.Controllers
{
    [Route("api/[controller]/generate")]
    [ApiController]
    public class DocumentController : ControllerBase
    {
        private readonly AuthenticationService authenticationService;
        private readonly DocumentService documentService;

        public DocumentController(DocumentService documentService, AuthenticationService authenticationService)
        {
            this.authenticationService = authenticationService;
            this.documentService = documentService;
        }

        [HttpPost]
        [Route("{email}")]
        public async Task<ResponseModel> generateDocument([FromForm] FileUploadDto fileUploadDto, string email)
        {
            await authenticationService.setCredential();
            await documentService.executeDocumentProcess(fileUploadDto, email);
            return new ResponseModel() { 
                isSuccess = true,
                statusCode = 200,
                message = $"The file named {fileUploadDto.file.FileName} converted to {FileGenerationConstants.PDF_OUTPUT_FORMAT} format succesfully. Sent to {email} address." 
            };
        }
    }
}
