package pl.unkx.jobtrack.reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
class ReminderService {

    private static final Logger log = LoggerFactory.getLogger(ReminderService.class);

    private final ReminderRepository repository;
    private final Clock clock;
    private final int afterDays;

    ReminderService(ReminderRepository repository, Clock clock,
                    @Value("${jobtrack.reminders.after-days}") int afterDays) {
        this.repository = repository;
        this.clock = clock;
        this.afterDays = afterDays;
    }

    @Scheduled(initialDelayString = "${jobtrack.reminders.initial-delay}",
            fixedDelayString = "${jobtrack.reminders.interval}")
    void createDueReminders() {
        LocalDate cutoff = LocalDate.now(clock).minusDays(afterDays);
        int created = repository.createForApplicationsWaitingSince(cutoff, Instant.now(clock));
        if (created > 0) {
            log.info("Created {} follow-up reminder(s) for applications sent on or before {}", created, cutoff);
        }
    }

    @Transactional(readOnly = true)
    List<ReminderResponse> findUnread(Long userId) {
        return repository.findUnreadForUser(userId);
    }

    void markRead(Long userId, Long id) {
        if (repository.markRead(id, userId, Instant.now(clock)) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reminder " + id + " not found");
        }
    }
}
