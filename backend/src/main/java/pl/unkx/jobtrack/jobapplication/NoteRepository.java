package pl.unkx.jobtrack.jobapplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByJobApplicationIdOrderByCreatedAtDescIdDesc(Long jobApplicationId);

    Optional<Note> findByIdAndJobApplicationId(Long id, Long jobApplicationId);
}
