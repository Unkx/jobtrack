package pl.unkx.jobtrack.stats;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.unkx.jobtrack.jobapplication.Source;
import pl.unkx.jobtrack.jobapplication.Status;
import pl.unkx.jobtrack.stats.StatsResponse.SourceStats;
import pl.unkx.jobtrack.stats.StatsResponse.WeekCount;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * Statistics are derived from the status history, not from the current status:
 * an application "got a response" once it has any status change, and an offer
 * counts if it ever reached OFERTA.
 */
@Service
@Transactional(readOnly = true)
class StatsService {

    static final int WEEKS = 8;

    private final StatsRepository repository;
    private final Clock clock;

    StatsService(StatsRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    StatsResponse forUser(Long userId) {
        List<ApplicationRow> applications = repository.findApplications(userId);
        Map<Long, List<ChangeRow>> changes = repository.findStatusChanges(userId).stream()
                .collect(Collectors.groupingBy(ChangeRow::applicationId));

        return new StatsResponse(
                applications.size(),
                responseRate(applications, changes),
                averageDaysToFirstResponse(applications, changes),
                applicationsPerWeek(applications),
                bySource(applications, changes));
    }

    private List<WeekCount> applicationsPerWeek(List<ApplicationRow> applications) {
        LocalDate currentWeek = LocalDate.now(clock).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        List<WeekCount> weeks = new ArrayList<>();
        for (int i = WEEKS - 1; i >= 0; i--) {
            LocalDate start = currentWeek.minusWeeks(i);
            LocalDate end = start.plusWeeks(1);
            long count = applications.stream()
                    .filter(a -> !a.appliedAt().isBefore(start) && a.appliedAt().isBefore(end))
                    .count();
            weeks.add(new WeekCount(start, count));
        }
        return weeks;
    }

    private Double averageDaysToFirstResponse(List<ApplicationRow> applications, Map<Long, List<ChangeRow>> changes) {
        OptionalDouble average = applications.stream()
                .filter(a -> changes.containsKey(a.id()))
                .mapToLong(a -> {
                    ChangeRow first = changes.get(a.id()).stream()
                            .min(Comparator.comparing(ChangeRow::changedAt))
                            .orElseThrow();
                    LocalDate respondedOn = LocalDate.ofInstant(first.changedAt(), clock.getZone());
                    return ChronoUnit.DAYS.between(a.appliedAt(), respondedOn);
                })
                .average();
        return average.isPresent() ? average.getAsDouble() : null;
    }

    private List<SourceStats> bySource(List<ApplicationRow> applications, Map<Long, List<ChangeRow>> changes) {
        return Arrays.stream(Source.values())
                .map(source -> {
                    List<ApplicationRow> fromSource = applications.stream().filter(a -> a.source() == source).toList();
                    long responses = fromSource.stream().filter(a -> changes.containsKey(a.id())).count();
                    long offers = fromSource.stream()
                            .filter(a -> changes.getOrDefault(a.id(), List.of()).stream()
                                    .anyMatch(c -> c.toStatus() == Status.OFERTA))
                            .count();
                    return new SourceStats(source, fromSource.size(), responses, offers,
                            responseRate(fromSource, changes));
                })
                .toList();
    }

    private static Double responseRate(List<ApplicationRow> applications, Map<Long, List<ChangeRow>> changes) {
        if (applications.isEmpty()) {
            return null;
        }
        long responded = applications.stream().filter(a -> changes.containsKey(a.id())).count();
        return responded * 100.0 / applications.size();
    }
}
