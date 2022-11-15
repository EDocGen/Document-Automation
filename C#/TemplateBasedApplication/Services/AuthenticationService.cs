using System;
using System.Threading.Tasks;
using TemplateBasedApplication.Helper.Constants;

namespace TemplateBasedApplication.Services
{
    public class AuthenticationService
    {
        private readonly CacheService cacheService;
        private readonly LoginService loginService;
        public AuthenticationService(CacheService cacheService, LoginService loginService)
        {
            this.cacheService = cacheService;
            this.loginService = loginService;
        }
        public async Task setCredential()
        {
            if (isNotTokenCached())
            {
                await setTokenToCache();
            }
        }

        private bool isNotTokenCached()
        {
            var result = cacheService.Get<string>(RedisConstant.PDF_GENERATOR_TOKEN);
            return string.IsNullOrEmpty(result);
        }

        private async Task setTokenToCache()
        {
            var token = await loginService.loginAccount();
            cacheService.Set<String>(RedisConstant.PDF_GENERATOR_TOKEN, token, TimeSpan.FromHours(3));
        }
    }
}
