package pl.unkx.jobtrack.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.unkx.jobtrack.TestcontainersConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void requestWithoutTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/applications")).andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithInvalidTokenIsRejected() throws Exception {
        mockMvc.perform(get("/api/applications").header(HttpHeaders.AUTHORIZATION, "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithTokenFromLoginIsAccepted() throws Exception {
        String auth = TestUsers.registerAndLogin(mockMvc);

        mockMvc.perform(get("/api/applications").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk());
    }

    @Test
    void loginReturnsToken() throws Exception {
        String body = TestUsers.credentials(TestUsers.uniqueEmail(), "password123");
        register(body);

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void loginWithWrongPasswordOrUnknownEmailIsRejected() throws Exception {
        String email = TestUsers.uniqueEmail();
        register(TestUsers.credentials(email, "password123"));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(TestUsers.credentials(email, "wrong-password")))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(TestUsers.credentials(TestUsers.uniqueEmail(), "password123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registeringSameEmailTwiceIsConflict() throws Exception {
        String body = TestUsers.credentials(TestUsers.uniqueEmail(), "password123");
        register(body);

        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void registerRejectsInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(TestUsers.credentials("not-an-email", "password123")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(TestUsers.credentials(TestUsers.uniqueEmail(), "short")))
                .andExpect(status().isBadRequest());
    }

    private void register(String body) throws Exception {
        mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated());
    }
}
