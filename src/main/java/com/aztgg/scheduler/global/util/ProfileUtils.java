package com.aztgg.scheduler.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileUtils {

    private final Environment environment;

    public boolean isLocal() {
        return environment.acceptsProfiles(Profiles.of("local"));
    }
}
