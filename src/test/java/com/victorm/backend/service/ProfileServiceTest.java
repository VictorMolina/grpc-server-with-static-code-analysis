package com.victorm.backend.service;

import com.victorm.backend.GameServer;
import com.victorm.backend.exception.ErrorCode;
import com.victorm.backend.exception.GameException;
import com.victorm.backend.model.Profile;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @BeforeClass
    public static void init() {
        GameServer.startEmbeddedRedisServer();
    }

    @AfterClass
    public static void finish() {
        GameServer.stopEmbeddedRedisServer();
    }

    @Test
    public void test() {
        UUID uuid = UUID.randomUUID();

        profileService.createOrUpdate(new Profile(uuid, "Test"));
        Profile user = profileService.get(uuid);
        assertEquals("User UUID do not match", uuid, user.getUuid());
        assertEquals("User name do not match", "Test", user.getName());

        profileService.remove(uuid);

        try {
            profileService.get(uuid);
            fail("Get call should throw a PROFILE_NOT_FOUND exception");
        } catch (GameException e) {
            assertEquals("Wrong GameException", ErrorCode.PROFILE_NOT_FOUND, e.getErrorCode());
        }
    }
}
