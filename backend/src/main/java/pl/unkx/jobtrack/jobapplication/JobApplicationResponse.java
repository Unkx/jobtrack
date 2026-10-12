package pl.unkx.jobtrack.jobapplication;

import java.time.LocalDate;

public record JobApplicationResponse(
        Long id,
        String company,
        String position,
        String offerUrl,
        Integer salaryMin,
        Integer salaryMax,
        Source source,
        LocalDate appliedAt,
        Status status) {

    static JobApplicationResponse from(JobApplication application) {
        return new JobApplicationResponse(
                application.getId(),
                application.getCompany(),
                application.getPosition(),
                application.getOfferUrl(),
                application.getSalaryMin(),
                application.getSalaryMax(),
                application.getSource(),
                application.getAppliedAt(),
                application.getStatus());
    }
}
