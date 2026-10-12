package pl.unkx.jobtrack.jobapplication;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

public record JobApplicationRequest(
        @NotBlank @Size(max = 255) String company,
        @NotBlank @Size(max = 255) String position,
        @URL @Size(max = 2048) String offerUrl,
        @PositiveOrZero Integer salaryMin,
        @PositiveOrZero Integer salaryMax,
        @NotNull Source source,
        @NotNull LocalDate appliedAt) {

    @AssertTrue(message = "salaryMin must not be greater than salaryMax")
    boolean isSalaryRangeValid() {
        return salaryMin == null || salaryMax == null || salaryMin <= salaryMax;
    }
}
