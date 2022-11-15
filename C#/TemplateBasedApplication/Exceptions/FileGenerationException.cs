using System;

namespace TemplateBasedApplication.Exceptions
{
    public class FileGenerationException : Exception
    {
        public FileGenerationException(string message):base(message)
        {

        }
    }
}
