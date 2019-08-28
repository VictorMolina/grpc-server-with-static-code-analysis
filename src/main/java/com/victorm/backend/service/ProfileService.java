package com.victorm.backend.service;

import com.victorm.backend.model.Profile;
import com.victorm.backend.repository.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile get(UUID uuid) {
        return profileRepository.get(uuid);
    }

    public void createOrUpdate(Profile profile) {
        profileRepository.createOrUpdate(profile);
    }

    public void remove(UUID uuid) {
        profileRepository.remove(uuid);
    }
}
