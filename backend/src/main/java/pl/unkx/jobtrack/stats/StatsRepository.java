package pl.unkx.jobtrack.stats;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pl.unkx.jobtrack.jobapplication.JobApplication;

import java.util.List;

interface StatsRepository extends Repository<JobApplication, Long> {

    @Query("""
            SELECT new pl.unkx.jobtrack.stats.ApplicationRow(a.id, a.source, a.appliedAt)
            FROM JobApplication a
            WHERE a.user.id = :userId
            """)
    List<ApplicationRow> findApplications(Long userId);

    @Query("""
            SELECT new pl.unkx.jobtrack.stats.ChangeRow(s.jobApplication.id, s.toStatus, s.changedAt)
            FROM StatusChange s
            WHERE s.jobApplication.user.id = :userId
            """)
    List<ChangeRow> findStatusChanges(Long userId);
}
