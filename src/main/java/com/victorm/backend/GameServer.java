package com.victorm.backend;

import com.google.common.annotations.VisibleForTesting;
import com.victorm.backend.config.EnvironmentConfig;
import com.victorm.backend.config.GrpcServerConfig;
import com.victorm.backend.config.RedisConfig;
import com.victorm.backend.exception.GameExceptionInterceptor;
import com.victorm.backend.processor.GrpcProcessor;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@SuppressWarnings({"PMD.UseUtilityClass", "PMD.DoNotUseThreads"})
public class GameServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServer.class);

    private static RedisServer redisServer;
    private static Server grpcServer;
    private static boolean running;

    public static void main(String[] args) throws IOException, InterruptedException {
        start();
    }

    public static void start() throws IOException, InterruptedException {
        startEmbeddedRedisServer();
        startGrpcServer();
    }

    public static void startEmbeddedRedisServer() {
        ApplicationContext ctx = SpringApplication.run(GameServer.class);
        EnvironmentConfig environment = ctx.getBean(EnvironmentConfig.class);
        RedisConfig redisServerConfig = environment.getRedis();

        if (redisServerConfig.isEmbedded()) {
            GrpcServerConfig grpcServerConfig = environment.getGrpcServer();
            redisServer = RedisServer.builder()
                    .setting("bind 127.0.0.1")
                    .port(redisServerConfig.getPort())
                    .build();
            redisServer.start();
            LOGGER.info("Embedded Redis Server started on ports {} for game {} at {}",
                    redisServer.ports(), grpcServerConfig.getGameId(), new Date());
        }
    }

    public static void startGrpcServer() throws IOException, InterruptedException {
        ApplicationContext ctx = SpringApplication.run(GameServer.class);
        EnvironmentConfig environment = ctx.getBean(EnvironmentConfig.class);
        GrpcServerConfig grpcServerConfig = environment.getGrpcServer();

        ServerBuilder serverBuilder = ServerBuilder.forPort(grpcServerConfig.getPort());
        ctx.getBeansWithAnnotation(GrpcProcessor.class).values()
                .forEach(processor -> serverBuilder.addService((BindableService) processor));
        serverBuilder.intercept(new GameExceptionInterceptor());
        grpcServer = serverBuilder.build();

        grpcServer.start();
        LOGGER.info("GRPC Server started on port {} for game {} at {}",
                grpcServerConfig.getPort(), grpcServerConfig.getGameId(), new Date());
        running = true;
        grpcServer.awaitTermination();
    }

    public static void stop() {
        stopGrpcServer();
        stopEmbeddedRedisServer();
    }

    public static void stopEmbeddedRedisServer() {
        if (redisServer != null && redisServer.isActive()) {
            LOGGER.info("Stopping Embedded Redis Server");
            redisServer.stop();
        }

        running = false;
    }

    public static void stopGrpcServer() {
        if (grpcServer != null && !grpcServer.isShutdown()) {
            LOGGER.info("Stopping GRPC server");
            grpcServer.shutdownNow();
        }
        running = false;
    }

    @VisibleForTesting
    public static void startForTesting() {
        new Thread(() -> {
            try {
                GameServer.main(new String[0]);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        while (!running) {
            try {
                TimeUnit.MILLISECONDS.sleep(200L);
            } catch (InterruptedException ignored) {
                LOGGER.error("Waiting for GRPC Server start up");
            }
        }
    }
}
