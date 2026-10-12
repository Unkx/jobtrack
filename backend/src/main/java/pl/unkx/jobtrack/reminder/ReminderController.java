package pl.unkx.jobtrack.reminder;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
class ReminderController {

    private final ReminderService service;

    ReminderController(ReminderService service) {
        this.service = service;
    }

    @GetMapping
    List<ReminderResponse> findUnread(@AuthenticationPrincipal Jwt jwt) {
        return service.findUnread(Long.valueOf(jwt.getSubject()));
    }

    @PostMapping("/{id}/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void markRead(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
        service.markRead(Long.valueOf(jwt.getSubject()), id);
    }
}
