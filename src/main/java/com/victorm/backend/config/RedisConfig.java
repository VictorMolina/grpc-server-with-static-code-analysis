package com.victorm.backend.config;

@SuppressWarnings({"PMD.DataClass"})
public class RedisConfig {
    private boolean embedded;
    private String host;
    private int port;

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "RedisConfig{" +
                "embedded=" + embedded +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
