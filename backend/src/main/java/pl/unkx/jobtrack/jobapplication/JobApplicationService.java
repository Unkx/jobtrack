package pl.unkx.jobtrack.jobapplication;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.unkx.jobtrack.user.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
class JobApplicationService {

    private final JobApplicationRepository repository;
    private final UserRepository userRepository;
    private final StatusChangeRepository statusChangeRepository;
    private final NoteRepository noteRepository;

    JobApplicationService(JobApplicationRepository repository, UserRepository userRepository,
                          StatusChangeRepository statusChangeRepository, NoteRepository noteRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.statusChangeRepository = statusChangeRepository;
        this.noteRepository = noteRepository;
    }

    @Transactional(readOnly = true)
    List<JobApplicationResponse> findAll(Long userId) {
        return repository.findAllByUserId(userId, Sort.by(Sort.Direction.DESC, "appliedAt")).stream()
                .map(JobApplicationResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    JobApplicationResponse findById(Long userId, Long id) {
        return JobApplicationResponse.from(getOrThrow(userId, id));
    }

    JobApplicationResponse create(Long userId, JobApplicationRequest request) {
        JobApplication application = new JobApplication(userRepository.getReferenceById(userId), request);
        return JobApplicationResponse.from(repository.save(application));
    }

    JobApplicationResponse update(Long userId, Long id, JobApplicationRequest request) {
        JobApplication application = getOrThrow(userId, id);
        application.apply(request);
        return JobApplicationResponse.from(application);
    }

    JobApplicationResponse changeStatus(Long userId, Long id, Status status) {
        JobApplication application = getOrThrow(userId, id);
        if (application.getStatus() != status) {
            statusChangeRepository.save(new StatusChange(application, application.getStatus(), status, Instant.now()));
            application.setStatus(status);
        }
        return JobApplicationResponse.from(application);
    }

    void delete(Long userId, Long id) {
        repository.delete(getOrThrow(userId, id));
    }

    @Transactional(readOnly = true)
    List<StatusChangeResponse> history(Long userId, Long id) {
        return statusChangeRepository.findAllByJobApplicationIdOrderByChangedAt(getOrThrow(userId, id).getId()).stream()
                .map(StatusChangeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    List<NoteResponse> notes(Long userId, Long id) {
        return noteRepository.findAllByJobApplicationIdOrderByCreatedAtDescIdDesc(getOrThrow(userId, id).getId()).stream()
                .map(NoteResponse::from)
                .toList();
    }

    NoteResponse addNote(Long userId, Long id, String content) {
        Note note = new Note(getOrThrow(userId, id), content.strip(), Instant.now());
        return NoteResponse.from(noteRepository.save(note));
    }

    void deleteNote(Long userId, Long id, Long noteId) {
        JobApplication application = getOrThrow(userId, id);
        Note note = noteRepository.findByIdAndJobApplicationId(noteId, application.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Note " + noteId + " not found"));
        noteRepository.delete(note);
    }

    // Another user's application is reported as missing so its existence is not revealed
    private JobApplication getOrThrow(Long userId, Long id) {
        return repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Job application " + id + " not found"));
    }
}
