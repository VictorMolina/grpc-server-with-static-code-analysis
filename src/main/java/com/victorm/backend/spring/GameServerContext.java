package com.victorm.backend.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorm.backend.config.EnvironmentConfig;
import com.victorm.backend.config.RedisConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class GameServerContext {

    private final EnvironmentConfig environment;

    public GameServerContext(EnvironmentConfig environment) {
        this.environment = environment;
    }

    @Bean
    public Jedis getJedisClient() {
        RedisConfig redis = environment.getRedis();
        return new Jedis(redis.isEmbedded() ? "localhost" : redis.getHost(), redis.getPort());
    }

    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public TerminateBean getTerminateBean() {
        return new TerminateBean();
    }
}
