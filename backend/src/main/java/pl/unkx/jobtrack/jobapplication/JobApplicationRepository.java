package pl.unkx.jobtrack.jobapplication;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    List<JobApplication> findAllByUserId(Long userId, Sort sort);

    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
}
