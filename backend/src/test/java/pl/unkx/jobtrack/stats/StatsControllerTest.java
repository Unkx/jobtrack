package pl.unkx.jobtrack.stats;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.unkx.jobtrack.TestcontainersConfiguration;
import pl.unkx.jobtrack.auth.TestUsers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void statsAreComputedFromCurrentUsersHistoryOnly() throws Exception {
        String auth = TestUsers.registerAndLogin(mockMvc);
        String responded = create(auth, "LINKEDIN");
        create(auth, "LINKEDIN");
        mockMvc.perform(patch(responded + "/status").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"OFERTA\"}"))
                .andExpect(status().isOk());
        // another user's data must not leak into the numbers
        create(TestUsers.registerAndLogin(mockMvc), "REFERRAL");

        mockMvc.perform(get("/api/stats").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalApplications").value(2))
                .andExpect(jsonPath("$.responseRate").value(50.0))
                .andExpect(jsonPath("$.averageDaysToFirstResponse").value(0.0))
                .andExpect(jsonPath("$.applicationsPerWeek.length()").value(8))
                .andExpect(jsonPath("$.applicationsPerWeek[7].count").value(2))
                .andExpect(jsonPath("$.bySource[0].source").value("LINKEDIN"))
                .andExpect(jsonPath("$.bySource[0].offers").value(1))
                .andExpect(jsonPath("$.bySource[2].applications").value(0));
    }

    @Test
    void statsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/stats")).andExpect(status().isUnauthorized());
    }

    private String create(String auth, String source) throws Exception {
        String body = """
                {"company": "Acme", "position": "Dev", "source": "%s", "appliedAt": "%s"}
                """.formatted(source, LocalDate.now());
        return mockMvc.perform(post("/api/applications").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }
}
