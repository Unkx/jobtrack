package pl.unkx.jobtrack.jobapplication;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import pl.unkx.jobtrack.user.User;

import java.time.LocalDate;

@Entity
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String company;
    private String position;
    private String offerUrl;
    private Integer salaryMin;
    private Integer salaryMax;

    @Enumerated(EnumType.STRING)
    private Source source;

    private LocalDate appliedAt;

    @Enumerated(EnumType.STRING)
    private Status status = Status.WYSLANO;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    protected JobApplication() {
    }

    JobApplication(User user, JobApplicationRequest request) {
        this.user = user;
        apply(request);
    }

    void apply(JobApplicationRequest request) {
        company = request.company();
        position = request.position();
        offerUrl = request.offerUrl();
        salaryMin = request.salaryMin();
        salaryMax = request.salaryMax();
        source = request.source();
        appliedAt = request.appliedAt();
    }

    void setStatus(Status status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public String getPosition() {
        return position;
    }

    public String getOfferUrl() {
        return offerUrl;
    }

    public Integer getSalaryMin() {
        return salaryMin;
    }

    public Integer getSalaryMax() {
        return salaryMax;
    }

    public Source getSource() {
        return source;
    }

    public LocalDate getAppliedAt() {
        return appliedAt;
    }

    public Status getStatus() {
        return status;
    }
}
