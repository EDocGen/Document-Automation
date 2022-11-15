using Microsoft.AspNetCore.Http;

namespace TemplateBasedApplication.Models.Dto
{
    public class FileUploadDto
    {
        public IFormFile file { get; set; }
    }
}
