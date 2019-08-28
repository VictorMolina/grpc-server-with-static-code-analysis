package com.victorm.backend.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.victorm.backend.exception.ErrorCode;
import com.victorm.backend.exception.GameException;
import com.victorm.backend.exception.ThrowingFunction;
import com.victorm.backend.model.Profile;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.UUID;

@Repository
public class ProfileRepository {
    private static final String REDIS_PROFILE_KEY = "profile";

    private final Jedis jedis;
    private final ThrowingFunction<String, Profile, GameException> deserializeProfileFunc;
    private final ThrowingFunction<Profile, String, GameException> serializeProfileFunc;


    public ProfileRepository(Jedis jedis, ObjectMapper mapper) {
        this.jedis = jedis;
        this.deserializeProfileFunc = profile -> {
            try {
                return mapper.readValue(profile, Profile.class);
            } catch (IOException e) {
                throw new GameException(ErrorCode.READ_JSON_OBJECT, "Error reading profile", e);
            }
        };
        this.serializeProfileFunc = profile -> {
            try {
                return mapper.writeValueAsString(profile);
            } catch (IOException e) {
                throw new GameException(ErrorCode.WRITE_JSON_OBJECT, "Error writing profile", e);
            }
        };
    }

    public Profile get(UUID uuid) {
        String redisKey = getRedisKey(uuid);
        String redisValue = jedis.get(redisKey);
        if (redisValue == null) {
            throw new GameException(ErrorCode.PROFILE_NOT_FOUND, String.format("Profile %s not found", uuid));
        }
        return deserializeProfileFunc.apply(redisValue);
    }

    public void createOrUpdate(Profile profile) {
        String redisKey = getRedisKey(profile.getUuid());
        String redisValue = serializeProfileFunc.apply(profile);
        jedis.set(redisKey, redisValue);
    }

    public void remove(UUID uuid) {
        String redisKey = getRedisKey(uuid);
        Long removedProfiles = jedis.del(redisKey);
        if (removedProfiles <= 0) {
            throw new GameException(ErrorCode.PROFILE_NOT_FOUND, String.format("Profile %s not found", uuid));
        }
    }

    private String getRedisKey(UUID uuid) {
        return String.format("%s:%s", REDIS_PROFILE_KEY, uuid.toString());
    }
}
