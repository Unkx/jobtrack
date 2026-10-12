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
import org.springframework.test.web.servlet.ResultActions;
import pl.unkx.jobtrack.TestcontainersConfiguration;
import pl.unkx.jobtrack.auth.TestUsers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class NoteAndHistoryControllerTest {

    private static final String BODY = """
            {"company": "Acme", "position": "Junior Java Developer", "source": "LINKEDIN", "appliedAt": "2026-10-01"}
            """;

    @Autowired
    MockMvc mockMvc;

    private String auth;
    private String location;

    @BeforeEach
    void createApplication() throws Exception {
        auth = TestUsers.registerAndLogin(mockMvc);
        location = mockMvc.perform(post("/api/applications").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");
    }

    @Test
    void notesCanBeAddedListedNewestFirstAndDeleted() throws Exception {
        addNote(auth, "Rozmowa z Anną z HR, stack: Spring Boot").andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Rozmowa z Anną z HR, stack: Spring Boot"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
        String second = addNote(auth, "Wysłać follow-up w piątek").andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Integer secondId = com.jayway.jsonpath.JsonPath.read(second, "$.id");

        mockMvc.perform(get(location + "/notes").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Wysłać follow-up w piątek"));

        mockMvc.perform(delete(location + "/notes/" + secondId).header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(location + "/notes").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void blankNoteIsRejected() throws Exception {
        addNote(auth, "   ").andExpect(status().isBadRequest());
    }

    @Test
    void anotherUserCannotReadAddOrDeleteNotes() throws Exception {
        String note = addNote(auth, "prywatne").andReturn().getResponse().getContentAsString();
        Integer noteId = com.jayway.jsonpath.JsonPath.read(note, "$.id");
        String other = TestUsers.registerAndLogin(mockMvc);

        mockMvc.perform(get(location + "/notes").header(HttpHeaders.AUTHORIZATION, other))
                .andExpect(status().isNotFound());
        addNote(other, "obce").andExpect(status().isNotFound());
        mockMvc.perform(delete(location + "/notes/" + noteId).header(HttpHeaders.AUTHORIZATION, other))
                .andExpect(status().isNotFound());
    }

    @Test
    void historyListsStatusChangesInOrder() throws Exception {
        changeStatus("ROZMOWA_HR");
        changeStatus("TECHNICZNA");

        mockMvc.perform(get(location + "/history").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].fromStatus").value("WYSLANO"))
                .andExpect(jsonPath("$[0].toStatus").value("ROZMOWA_HR"))
                .andExpect(jsonPath("$[1].toStatus").value("TECHNICZNA"))
                .andExpect(jsonPath("$[1].changedAt").isNotEmpty());
    }

    @Test
    void historyOfAnotherUsersApplicationIsNotFound() throws Exception {
        mockMvc.perform(get(location + "/history").header(HttpHeaders.AUTHORIZATION, TestUsers.registerAndLogin(mockMvc)))
                .andExpect(status().isNotFound());
    }

    private ResultActions addNote(String authorization, String content) throws Exception {
        return mockMvc.perform(post(location + "/notes").header(HttpHeaders.AUTHORIZATION, authorization)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\": \"%s\"}".formatted(content)));
    }

    private void changeStatus(String newStatus) throws Exception {
        mockMvc.perform(patch(location + "/status").header(HttpHeaders.AUTHORIZATION, auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"%s\"}".formatted(newStatus)))
                .andExpect(status().isOk());
    }
}
