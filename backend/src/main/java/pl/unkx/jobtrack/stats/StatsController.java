package pl.unkx.jobtrack.stats;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
class StatsController {

    private final StatsService service;

    StatsController(StatsService service) {
        this.service = service;
    }

    @GetMapping
    StatsResponse stats(@AuthenticationPrincipal Jwt jwt) {
        return service.forUser(Long.valueOf(jwt.getSubject()));
    }
}
