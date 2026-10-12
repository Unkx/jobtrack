package pl.unkx.jobtrack.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.unkx.jobtrack.stats.StatsResponse.SourceStats;
import pl.unkx.jobtrack.stats.StatsResponse.WeekCount;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.when;
import static pl.unkx.jobtrack.jobapplication.Source.JUST_JOIN_IT;
import static pl.unkx.jobtrack.jobapplication.Source.LINKEDIN;
import static pl.unkx.jobtrack.jobapplication.Source.REFERRAL;
import static pl.unkx.jobtrack.jobapplication.Status.ODRZUCONO;
import static pl.unkx.jobtrack.jobapplication.Status.OFERTA;
import static pl.unkx.jobtrack.jobapplication.Status.ROZMOWA_HR;
import static pl.unkx.jobtrack.jobapplication.Status.TECHNICZNA;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    private static final long USER_ID = 1L;
    // Wednesday; the current week starts on Monday 2026-10-12
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-14T12:00:00Z"), ZoneOffset.UTC);

    @Mock
    StatsRepository repository;

    StatsService service;

    @BeforeEach
    void setUp() {
        service = new StatsService(repository, CLOCK);
    }

    /*
     * 1 LINKEDIN     sent 10-12, no response
     * 2 LINKEDIN     sent 10-05, HR after 3 days, then technical, then offer
     * 3 JUST_JOIN_IT sent 10-06, rejected after 1 day
     * 4 REFERRAL     sent 08-03 (outside the 8-week window), HR after 5 days
     * 5 JUST_JOIN_IT sent 10-13, no response
     */
    private void givenKnownHistory() {
        when(repository.findApplications(USER_ID)).thenReturn(List.of(
                new ApplicationRow(1L, LINKEDIN, LocalDate.parse("2026-10-12")),
                new ApplicationRow(2L, LINKEDIN, LocalDate.parse("2026-10-05")),
                new ApplicationRow(3L, JUST_JOIN_IT, LocalDate.parse("2026-10-06")),
                new ApplicationRow(4L, REFERRAL, LocalDate.parse("2026-08-03")),
                new ApplicationRow(5L, JUST_JOIN_IT, LocalDate.parse("2026-10-13"))));
        // deliberately not in chronological order
        when(repository.findStatusChanges(USER_ID)).thenReturn(List.of(
                new ChangeRow(2L, OFERTA, Instant.parse("2026-10-13T09:00:00Z")),
                new ChangeRow(2L, ROZMOWA_HR, Instant.parse("2026-10-08T10:00:00Z")),
                new ChangeRow(2L, TECHNICZNA, Instant.parse("2026-10-10T10:00:00Z")),
                new ChangeRow(3L, ODRZUCONO, Instant.parse("2026-10-07T09:00:00Z")),
                new ChangeRow(4L, ROZMOWA_HR, Instant.parse("2026-08-08T15:00:00Z"))));
    }

    @Test
    void responseRateCountsApplicationsWithAnyStatusChange() {
        givenKnownHistory();

        StatsResponse stats = service.forUser(USER_ID);

        assertThat(stats.totalApplications()).isEqualTo(5);
        assertThat(stats.responseRate()).isCloseTo(60.0, within(0.001));
    }

    @Test
    void averageDaysToFirstResponseUsesEarliestChange() {
        givenKnownHistory();

        StatsResponse stats = service.forUser(USER_ID);

        // (3 + 1 + 5) / 3
        assertThat(stats.averageDaysToFirstResponse()).isCloseTo(3.0, within(0.001));
    }

    @Test
    void applicationsPerWeekCoversLastEightWeeksStartingOnMonday() {
        givenKnownHistory();

        List<WeekCount> weeks = service.forUser(USER_ID).applicationsPerWeek();

        assertThat(weeks).hasSize(8);
        assertThat(weeks.getFirst().weekStart()).isEqualTo(LocalDate.parse("2026-08-24"));
        assertThat(weeks.getLast()).isEqualTo(new WeekCount(LocalDate.parse("2026-10-12"), 2));
        assertThat(weeks.get(6)).isEqualTo(new WeekCount(LocalDate.parse("2026-10-05"), 2));
        assertThat(weeks.stream().mapToLong(WeekCount::count).sum()).isEqualTo(4);
    }

    @Test
    void bySourceReportsResponsesAndOffersFromHistory() {
        givenKnownHistory();

        List<SourceStats> bySource = service.forUser(USER_ID).bySource();

        assertThat(bySource).containsExactly(
                new SourceStats(LINKEDIN, 2, 1, 1, 50.0),
                new SourceStats(JUST_JOIN_IT, 2, 1, 0, 50.0),
                new SourceStats(REFERRAL, 1, 1, 0, 100.0));
    }

    @Test
    void emptyHistoryGivesZeroCountsAndNoRates() {
        when(repository.findApplications(USER_ID)).thenReturn(List.of());
        when(repository.findStatusChanges(USER_ID)).thenReturn(List.of());

        StatsResponse stats = service.forUser(USER_ID);

        assertThat(stats.totalApplications()).isZero();
        assertThat(stats.responseRate()).isNull();
        assertThat(stats.averageDaysToFirstResponse()).isNull();
        assertThat(stats.applicationsPerWeek()).hasSize(8).allSatisfy(week -> assertThat(week.count()).isZero());
        assertThat(stats.bySource()).allSatisfy(source -> {
            assertThat(source.applications()).isZero();
            assertThat(source.responseRate()).isNull();
        });
    }
}
