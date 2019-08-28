package com.victorm.backend.exception;

import com.google.common.annotations.VisibleForTesting;
import com.victorm.backend.grpc.GameError;
import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;

@SuppressWarnings({"PMD.AvoidCatchingGenericException"})
public class GameExceptionInterceptor implements ServerInterceptor {

    @VisibleForTesting
    public static final Metadata.Key<GameError> GAME_ERROR =
            ProtoUtils.keyForProto(GameError.getDefaultInstance());

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        ServerCall.Listener<ReqT> delegate = next.startCall(call, headers);
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(delegate) {
            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (GameException e) {
                    closeCall(call, e.getErrorCode(), e);
                } catch (Exception e) {
                    closeCall(call, ErrorCode.UNKNOWN, e);
                }
            }
        };
    }

    private <ReqT, RespT> void closeCall(ServerCall<ReqT, RespT> call, ErrorCode errorCode, Throwable cause) {
        GameError gameError = GameError.newBuilder()
                .setCode(errorCode.getCode())
                .setDescription(errorCode.name())
                .build();
        Metadata metadata = new Metadata();
        metadata.put(GAME_ERROR, gameError);

        call.close(Status.INTERNAL
                .withCause(cause)
                .withDescription("GAME_ERROR"), metadata);
    }
}
