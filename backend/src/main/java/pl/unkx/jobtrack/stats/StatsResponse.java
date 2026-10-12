package pl.unkx.jobtrack.stats;

import pl.unkx.jobtrack.jobapplication.Source;

import java.time.LocalDate;
import java.util.List;

/** Rates are percentages (0-100); rate and average fields are null when there is nothing to divide by. */
public record StatsResponse(
        int totalApplications,
        Double responseRate,
        Double averageDaysToFirstResponse,
        List<WeekCount> applicationsPerWeek,
        List<SourceStats> bySource) {

    public record WeekCount(LocalDate weekStart, long count) {
    }

    public record SourceStats(Source source, long applications, long responses, long offers, Double responseRate) {
    }
}
