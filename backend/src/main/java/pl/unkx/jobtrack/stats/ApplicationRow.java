package pl.unkx.jobtrack.stats;

import pl.unkx.jobtrack.jobapplication.Source;

import java.time.LocalDate;

public record ApplicationRow(Long id, Source source, LocalDate appliedAt) {
}
