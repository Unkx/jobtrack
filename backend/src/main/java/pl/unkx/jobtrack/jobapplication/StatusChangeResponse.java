package pl.unkx.jobtrack.jobapplication;

import java.time.Instant;

record StatusChangeResponse(Status fromStatus, Status toStatus, Instant changedAt) {

    static StatusChangeResponse from(StatusChange change) {
        return new StatusChangeResponse(change.getFromStatus(), change.getToStatus(), change.getChangedAt());
    }
}
