package pl.unkx.jobtrack.jobapplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface StatusChangeRepository extends JpaRepository<StatusChange, Long> {

    List<StatusChange> findAllByJobApplicationIdOrderByChangedAt(Long jobApplicationId);
}
