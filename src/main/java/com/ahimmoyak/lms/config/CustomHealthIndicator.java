package com.ahimmoyak.lms.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isHealthy = checkCustomServiceHealth();
        if (isHealthy) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("Custom Service", "Not available")
                    .withDetail("Reason", "Service not reachable")
                    .build();
        }
    }

    private boolean checkCustomServiceHealth() {
        return true;
    }

}
