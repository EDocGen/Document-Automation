using Microsoft.Extensions.Configuration;
using StackExchange.Redis;

namespace TemplateBasedApplication.Cache.Redis
{
    public class RedisServer
    {
        private ConnectionMultiplexer _connectionMultiplexer;
        private IDatabase _database;
        private int _currentDb = 0;
        public RedisServer(IConfiguration configuration)
        {
            _connectionMultiplexer = ConnectionMultiplexer.Connect(configuration.GetSection("RedisSettings:RedisConnectionString").Value);
            _database = _connectionMultiplexer.GetDatabase(_currentDb);
        }

        public IDatabase Database => _database;
    }
}
