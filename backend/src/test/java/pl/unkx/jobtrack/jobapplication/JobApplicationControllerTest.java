package pl.unkx.jobtrack.jobapplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pl.unkx.jobtrack.TestcontainersConfiguration;
import pl.unkx.jobtrack.auth.TestUsers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class JobApplicationControllerTest {

    private static final String VALID_BODY = """
            {
              "company": "Acme",
              "position": "Junior Java Developer",
              "offerUrl": "https://justjoin.it/offers/acme-junior-java",
              "salaryMin": 7000,
              "salaryMax": 9000,
              "source": "JUST_JOIN_IT",
              "appliedAt": "2026-10-01"
            }
            """;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    StatusChangeRepository statusChangeRepository;

    private String auth;

    @BeforeEach
    void loginAsFreshUser() throws Exception {
        auth = TestUsers.registerAndLogin(mockMvc);
    }

    @Test
    void createdApplicationCanBeFetchedAndStartsAsWyslano() throws Exception {
        String location = create(VALID_BODY);

        mockMvc.perform(get(location).header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Acme"))
                .andExpect(jsonPath("$.position").value("Junior Java Developer"))
                .andExpect(jsonPath("$.salaryMin").value(7000))
                .andExpect(jsonPath("$.source").value("JUST_JOIN_IT"))
                .andExpect(jsonPath("$.appliedAt").value("2026-10-01"))
                .andExpect(jsonPath("$.status").value("WYSLANO"));
    }

    @Test
    void updateChangesFieldsAndDeleteRemovesApplication() throws Exception {
        String location = create(VALID_BODY);

        mockMvc.perform(put(location).header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY.replace("Acme", "Globex")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Globex"));

        mockMvc.perform(delete(location).header(HttpHeaders.AUTHORIZATION, auth)).andExpect(status().isNoContent());
        mockMvc.perform(get(location).header(HttpHeaders.AUTHORIZATION, auth)).andExpect(status().isNotFound());
    }

    @Test
    void listContainsCreatedApplication() throws Exception {
        create(VALID_BODY.replace("Acme", "Initech"));

        mockMvc.perform(get("/api/applications").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.company == 'Initech')]").exists());
    }

    @Test
    void userCannotSeeOrModifyAnotherUsersApplications() throws Exception {
        String location = create(VALID_BODY.replace("Acme", "Umbrella"));
        String otherAuth = TestUsers.registerAndLogin(mockMvc);

        mockMvc.perform(get("/api/applications").header(HttpHeaders.AUTHORIZATION, otherAuth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get(location).header(HttpHeaders.AUTHORIZATION, otherAuth))
                .andExpect(status().isNotFound());
        mockMvc.perform(put(location).header(HttpHeaders.AUTHORIZATION, otherAuth)
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isNotFound());
        mockMvc.perform(delete(location).header(HttpHeaders.AUTHORIZATION, otherAuth))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(location).header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.company").value("Umbrella"));
    }

    @Test
    void changingStatusUpdatesApplicationAndRecordsHistory() throws Exception {
        String location = create(VALID_BODY);
        Long id = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        changeStatus(location, auth, "ROZMOWA_HR")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ROZMOWA_HR"));
        changeStatus(location, auth, "TECHNICZNA").andExpect(status().isOk());

        List<StatusChange> history = statusChangeRepository.findAllByJobApplicationIdOrderByChangedAt(id);
        assertThat(history)
                .extracting(StatusChange::getFromStatus, StatusChange::getToStatus)
                .containsExactly(
                        tuple(Status.WYSLANO, Status.ROZMOWA_HR),
                        tuple(Status.ROZMOWA_HR, Status.TECHNICZNA));
        assertThat(history).allSatisfy(change -> assertThat(change.getChangedAt()).isNotNull());
    }

    @Test
    void settingSameStatusDoesNotRecordHistory() throws Exception {
        String location = create(VALID_BODY);
        Long id = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));

        changeStatus(location, auth, "WYSLANO").andExpect(status().isOk());

        assertThat(statusChangeRepository.findAllByJobApplicationIdOrderByChangedAt(id)).isEmpty();
    }

    @Test
    void changingStatusRejectsUnknownStatusAndForeignApplication() throws Exception {
        String location = create(VALID_BODY);

        changeStatus(location, auth, "ZATRUDNIONY").andExpect(status().isBadRequest());
        changeStatus(location, TestUsers.registerAndLogin(mockMvc), "OFERTA").andExpect(status().isNotFound());
    }

    @Test
    void deletingApplicationAlsoDeletesItsHistory() throws Exception {
        String location = create(VALID_BODY);
        Long id = Long.valueOf(location.substring(location.lastIndexOf('/') + 1));
        changeStatus(location, auth, "ODRZUCONO").andExpect(status().isOk());

        mockMvc.perform(delete(location).header(HttpHeaders.AUTHORIZATION, auth)).andExpect(status().isNoContent());

        assertThat(statusChangeRepository.findAllByJobApplicationIdOrderByChangedAt(id)).isEmpty();
    }

    @Test
    void rejectsInvalidRequest() throws Exception {
        String blankCompany = VALID_BODY.replace("\"Acme\"", "\"\"");
        String invertedSalary = VALID_BODY.replace("9000", "5000");

        mockMvc.perform(post("/api/applications").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content(blankCompany))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/applications").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content(invertedSalary))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returns404ForMissingApplication() throws Exception {
        mockMvc.perform(get("/api/applications/999999").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isNotFound());
    }

    private ResultActions changeStatus(String location, String authorization, String newStatus) throws Exception {
        return mockMvc.perform(patch(location + "/status").header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"%s\"}".formatted(newStatus)));
    }

    private String create(String body) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/applications").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return result.getResponse().getHeader("Location");
    }
}
