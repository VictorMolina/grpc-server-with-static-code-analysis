package com.victorm.backend.mapper;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileMapper {
    public com.victorm.backend.model.Profile map(com.victorm.backend.grpc.Profile profile) {
        return new com.victorm.backend.model.Profile(UUID.fromString(profile.getUuid()), profile.getName());
    }

    public com.victorm.backend.grpc.Profile map(com.victorm.backend.model.Profile profile) {
        return com.victorm.backend.grpc.Profile.newBuilder()
                .setUuid(profile.getUuid().toString())
                .setName(profile.getName())
                .build();
    }
}
