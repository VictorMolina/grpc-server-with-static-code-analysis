package com.victorm.backend.config;

@SuppressWarnings({"PMD.DataClass"})
public class GrpcServerConfig {
    private String gameId;
    private int port;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "GrpcServerConfig{" +
                "gameId='" + gameId + '\'' +
                ", port=" + port +
                '}';
    }
}
