package com.twa.flights.api.clusters.caching;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.google.common.collect.Lists;
import com.twa.flights.api.clusters.configuration.settings.CacheSettings;
import com.twa.flights.api.clusters.serializer.CitySerializer;

@Configuration
public class CacheManagerConfiguration {
    private static final String CATALOG_CITY = "catalog_city";
    private final JedisConnectionFactory jedisConnectionFactory;
    private final CitySerializer citySerializer;

    public CacheManagerConfiguration(JedisConnectionFactory jedisConnectionFactory, CitySerializer citySerializer) {
        super();
        this.jedisConnectionFactory = jedisConnectionFactory;
        this.citySerializer = citySerializer;
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(Lists.newArrayList(RedisCacheManager.builder(jedisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration()).build().getCache(CATALOG_CITY)));

        return simpleCacheManager;
    }

    private RedisCacheConfiguration redisCacheConfiguration() {
        CacheSettings cacheCitySettings = getCacheSettings(CATALOG_CITY);
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(citySerializer))
                .entryTtl(Duration.ofMinutes(cacheCitySettings.getExpireAfterWriteTime()));
    }

    private CacheSettings getCacheSettings(String cacheName) {
        CacheSettings cacheSettings = new CacheSettings();
        cacheSettings.setExpireAfterWriteTime(1);

        return cacheSettings;
    }
}
