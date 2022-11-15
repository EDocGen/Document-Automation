using System;

namespace TemplateBasedApplication.Exceptions
{
    public class InvalidServiceResponseException : Exception
    {
        public InvalidServiceResponseException(string message) : base(message)
        {

        }
    }
}
