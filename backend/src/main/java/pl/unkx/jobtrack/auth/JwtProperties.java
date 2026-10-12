package pl.unkx.jobtrack.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("jobtrack.jwt")
record JwtProperties(String secret, Duration ttl) {
}
