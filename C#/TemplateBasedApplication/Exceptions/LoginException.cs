using System;

namespace TemplateBasedApplication.Exceptions
{
    public class LoginException : Exception
    {
        public LoginException(string message):base(message)
        {

        }
    }
}
