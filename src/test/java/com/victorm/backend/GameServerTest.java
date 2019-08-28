package com.victorm.backend;

import com.victorm.backend.exception.ErrorCode;
import com.victorm.backend.exception.GameExceptionInterceptor;
import com.victorm.backend.grpc.GameError;
import com.victorm.backend.grpc.Get;
import com.victorm.backend.grpc.Persist;
import com.victorm.backend.grpc.Profile;
import com.victorm.backend.grpc.ProfileServiceGrpc;
import com.victorm.backend.grpc.Remove;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GameServerTest {

    private static ManagedChannel channel;

    @BeforeClass
    public static void init() {
        GameServer.startForTesting();
        channel = ManagedChannelBuilder.forAddress("localhost", 6666)
                .usePlaintext()
                .build();
    }

    @AfterClass
    public static void finish() {
        channel.shutdown();
        GameServer.stop();
    }

    @Test
    public void testProfileService() {
        ProfileServiceGrpc.ProfileServiceBlockingStub stubProfile
                = ProfileServiceGrpc.newBlockingStub(channel);

        UUID uuid = UUID.randomUUID();

        Persist.Response persistResponse = stubProfile.persist(Persist.Request.newBuilder()
                .setProfile(Profile.newBuilder()
                        .setUuid(uuid.toString())
                        .setName("Test Name"))
                .build());

        assertNotNull("Persist response is null", persistResponse);

        Get.Response getResponse = stubProfile.get(Get.Request.newBuilder()
                .setUuid(uuid.toString())
                .build());

        assertNotNull("Get response is null", getResponse);

        Optional<Profile> profile = getResponse.hasProfile() ? Optional.of(getResponse.getProfile()) : Optional.empty();

        assertNotNull("Profile is null", profile);
        assertTrue("Profile is not present", profile.isPresent());
        assertEquals("Profile UUID do not match", uuid, UUID.fromString(profile.get().getUuid()));
        assertEquals("Profile name do not match", "Test Name", profile.get().getName());

        UUID removeUuid = UUID.fromString(profile.get().getUuid());
        Remove.Response removeResponse = stubProfile.remove(Remove.Request.newBuilder()
                .setUuid(removeUuid.toString())
                .build());

        assertNotNull("Remove response is null", removeResponse);

        try {
            stubProfile.remove(Remove.Request.newBuilder()
                    .setUuid(removeUuid.toString())
                    .build());
            fail("Remove call should throw a PROFILE_NOT_FOUND exception");
        } catch (StatusRuntimeException e) {
            assertEquals("Incorrect exception code", ErrorCode.PROFILE_NOT_FOUND.getCode(), getErrorCode(e));
        }
    }

    private int getErrorCode(StatusRuntimeException statusRuntimeException) {
        Status status = statusRuntimeException.getStatus();
        assertNotNull("Status should not be null", status);
        assertEquals("Status should have a GAME_ERROR description",
                "GAME_ERROR", status.getDescription());

        GameError gameError = statusRuntimeException.getTrailers().get(GameExceptionInterceptor.GAME_ERROR);
        assertNotNull("GameError should not be null", gameError);
        assertEquals("Error code should be 1", 1, gameError.getCode());
        assertEquals("Error description should be PROFILE_NOT_FOUND",
                ErrorCode.PROFILE_NOT_FOUND.name(), gameError.getDescription());
        return gameError.getCode();
    }
}
