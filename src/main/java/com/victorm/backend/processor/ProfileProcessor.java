package com.victorm.backend.processor;


import com.victorm.backend.grpc.Get;
import com.victorm.backend.grpc.Persist;
import com.victorm.backend.grpc.ProfileServiceGrpc;
import com.victorm.backend.grpc.Remove;
import com.victorm.backend.mapper.ProfileMapper;
import com.victorm.backend.service.ProfileService;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

@GrpcProcessor
public class ProfileProcessor extends ProfileServiceGrpc.ProfileServiceImplBase {

    private final ProfileService profileService;
    private final ProfileMapper profileMapper;

    public ProfileProcessor(ProfileService profileService,
                            ProfileMapper profileMapper) {
        super();
        this.profileService = profileService;
        this.profileMapper = profileMapper;
    }

    @Override
    public void get(Get.Request request, StreamObserver<Get.Response> responseObserver) {
        UUID uuid = UUID.fromString(request.getUuid());

        com.victorm.backend.model.Profile profile = profileService.get(uuid);

        Get.Response.Builder response = Get.Response.newBuilder();
        response.setProfile(profileMapper.map(profile));

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void persist(Persist.Request request, StreamObserver<Persist.Response> responseObserver) {
        com.victorm.backend.model.Profile profile = profileMapper.map(request.getProfile());

        profileService.createOrUpdate(profile);

        Persist.Response.Builder response = Persist.Response.newBuilder();

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void remove(Remove.Request request, StreamObserver<Remove.Response> responseObserver) {
        UUID uuid = UUID.fromString(request.getUuid());

        profileService.remove(uuid);

        Remove.Response.Builder response = Remove.Response.newBuilder();

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}