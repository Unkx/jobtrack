package pl.unkx.jobtrack.stats;

import pl.unkx.jobtrack.jobapplication.Status;

import java.time.Instant;

public record ChangeRow(Long applicationId, Status toStatus, Instant changedAt) {
}
