package pl.unkx.jobtrack.reminder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // ON CONFLICT keeps this idempotent and safe if two runs overlap
    @Modifying
    @Query(nativeQuery = true, value = """
            INSERT INTO reminder (job_application_id, created_at)
            SELECT a.id, :now FROM job_application a
            WHERE a.status = 'WYSLANO' AND a.applied_at <= :cutoff
            ON CONFLICT (job_application_id) DO NOTHING
            """)
    int createForApplicationsWaitingSince(LocalDate cutoff, Instant now);

    @Query("""
            SELECT new pl.unkx.jobtrack.reminder.ReminderResponse(
                r.id, a.id, a.company, a.position, a.appliedAt, r.createdAt)
            FROM Reminder r JOIN r.jobApplication a
            WHERE a.user.id = :userId
              AND r.readAt IS NULL
              AND a.status = pl.unkx.jobtrack.jobapplication.Status.WYSLANO
            ORDER BY a.appliedAt
            """)
    List<ReminderResponse> findUnreadForUser(Long userId);

    @Modifying
    @Query("""
            UPDATE Reminder r SET r.readAt = :now
            WHERE r.id = :id
              AND r.jobApplication.id IN (SELECT a.id FROM JobApplication a WHERE a.user.id = :userId)
            """)
    int markRead(Long id, Long userId, Instant now);
}
