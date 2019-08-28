package com.victorm.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "environment")
@SuppressWarnings({"PMD.DataClass"})
public class EnvironmentConfig {
    private RedisConfig redis;
    private GrpcServerConfig grpcServer;

    public RedisConfig getRedis() {
        return redis;
    }

    public void setRedis(RedisConfig redis) {
        this.redis = redis;
    }

    public GrpcServerConfig getGrpcServer() {
        return grpcServer;
    }

    public void setGrpcServer(GrpcServerConfig grpcServer) {
        this.grpcServer = grpcServer;
    }

    @Override
    public String toString() {
        return "EnvironmentConfig{" +
                "redis=" + redis +
                ", grpcServer=" + grpcServer +
                '}';
    }
}
