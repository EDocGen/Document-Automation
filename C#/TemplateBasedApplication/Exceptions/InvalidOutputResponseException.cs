using System;

namespace TemplateBasedApplication.Exceptions
{
    public class InvalidOutputResponseException : Exception
    {
        public InvalidOutputResponseException(string message) : base(message)
        {

        }
    }
}
