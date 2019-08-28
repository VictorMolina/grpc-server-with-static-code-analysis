package com.victorm.backend.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Profile {
    private final UUID uuid;
    private final String name;

    @JsonCreator
    public Profile(@JsonProperty("uuid") UUID uuid,
                   @JsonProperty("name") String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                '}';
    }
}
