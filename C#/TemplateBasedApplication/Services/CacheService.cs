using System;

namespace TemplateBasedApplication.Services
{
    public interface CacheService
    {
        T Get<T>(string key) where T : class;
        void Set<T>(string key, T value, TimeSpan time) where T : class;
        bool IsSet(string key);
        void Clear(string key);
    }
}
