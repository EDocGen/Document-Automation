using System.Collections.Generic;

namespace TemplateBasedApplication.Models.Dto
{
    public class OutputModelDto
    {
        public int status { get; set; }
        public List<OutputDto> output { get; set; }
    }
}
