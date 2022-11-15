
using System.Text.Json;

namespace TemplateBasedApplication.Models.Dto
{
    public class ResponseModel
    {
        public bool isSuccess { get; set; }
        public int statusCode { get; set; }
        public string message { get; set; }

        public override string ToString()
        {
            return JsonSerializer.Serialize(this);
        }
    }
}
