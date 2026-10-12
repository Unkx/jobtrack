package pl.unkx.jobtrack.reminder;

import com.jayway.jsonpath.JsonPath;
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
class ReminderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReminderService reminderService;

    @Test
    void remindsOnlyAboutApplicationsStillWaitingLongerThanConfiguredDays() throws Exception {
        String auth = TestUsers.registerAndLogin(mockMvc);
        create(auth, "Stale", LocalDate.now().minusDays(10));
        create(auth, "Fresh", LocalDate.now().minusDays(2));
        String answered = create(auth, "Answered", LocalDate.now().minusDays(10));
        mockMvc.perform(patch(answered + "/status").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"status\": \"ROZMOWA_HR\"}"))
                .andExpect(status().isOk());

        reminderService.createDueReminders();
        reminderService.createDueReminders();

        mockMvc.perform(get("/api/reminders").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].company").value("Stale"));
    }

    @Test
    void markingReminderAsReadHidesItAndIsLimitedToOwner() throws Exception {
        String auth = TestUsers.registerAndLogin(mockMvc);
        create(auth, "Stale", LocalDate.now().minusDays(30));
        reminderService.createDueReminders();
        String reminders = mockMvc.perform(get("/api/reminders").header(HttpHeaders.AUTHORIZATION, auth))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(reminders, "$[0].id");

        mockMvc.perform(post("/api/reminders/{id}/read", id)
                        .header(HttpHeaders.AUTHORIZATION, TestUsers.registerAndLogin(mockMvc)))
                .andExpect(status().isNotFound());
        mockMvc.perform(post("/api/reminders/{id}/read", id).header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/reminders").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(jsonPath("$.length()").value(0));
    }

    private String create(String auth, String company, LocalDate appliedAt) throws Exception {
        String body = """
                {"company": "%s", "position": "Dev", "source": "LINKEDIN", "appliedAt": "%s"}
                """.formatted(company, appliedAt);
        return mockMvc.perform(post("/api/applications").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }
}
