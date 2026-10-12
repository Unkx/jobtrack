package pl.unkx.jobtrack.jobapplication;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
class JobApplicationController {

    private final JobApplicationService service;

    JobApplicationController(JobApplicationService service) {
        this.service = service;
    }

    @GetMapping
    List<JobApplicationResponse> findAll(@AuthenticationPrincipal Jwt jwt) {
        return service.findAll(userId(jwt));
    }

    @GetMapping("/{id}")
    JobApplicationResponse findById(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.findById(userId(jwt), id);
    }

    @PostMapping
    ResponseEntity<JobApplicationResponse> create(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody JobApplicationRequest request) {
        JobApplicationResponse created = service.create(userId(jwt), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    JobApplicationResponse update(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                  @Valid @RequestBody JobApplicationRequest request) {
        return service.update(userId(jwt), id, request);
    }

    @PatchMapping("/{id}/status")
    JobApplicationResponse changeStatus(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                                        @Valid @RequestBody StatusRequest request) {
        return service.changeStatus(userId(jwt), id, request.status());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        service.delete(userId(jwt), id);
    }

    @GetMapping("/{id}/history")
    List<StatusChangeResponse> history(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.history(userId(jwt), id);
    }

    @GetMapping("/{id}/notes")
    List<NoteResponse> notes(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        return service.notes(userId(jwt), id);
    }

    @PostMapping("/{id}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    NoteResponse addNote(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
                         @Valid @RequestBody NoteRequest request) {
        return service.addNote(userId(jwt), id, request.content());
    }

    @DeleteMapping("/{id}/notes/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteNote(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id, @PathVariable Long noteId) {
        service.deleteNote(userId(jwt), id, noteId);
    }

    private static Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
