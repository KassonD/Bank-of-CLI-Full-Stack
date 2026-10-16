package com.bankofcli.backend.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A single Clock bean, so services never call OffsetDateTime.now() directly.
 * Tests can inject a fixed clock and assert exact timestamps.
 */
@Configuration
public class TimeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
