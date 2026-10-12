package pl.unkx.jobtrack.reminder;

import java.time.Instant;
import java.time.LocalDate;

public record ReminderResponse(
        Long id,
        Long applicationId,
        String company,
        String position,
        LocalDate appliedAt,
        Instant createdAt) {
}
