package pl.unkx.jobtrack.reminder;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import pl.unkx.jobtrack.jobapplication.JobApplication;

import java.time.Instant;

/** Follow-up reminder for an application left in WYSLANO for too long. Rows are created by {@link ReminderService}. */
@Entity
class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private JobApplication jobApplication;

    private Instant createdAt;

    private Instant readAt;

    protected Reminder() {
    }
}
