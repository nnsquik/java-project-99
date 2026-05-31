package hexlet.code;

import hexlet.code.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testCreateUser() throws Exception {
        var body = """
                {
                    "email": "test@test.com",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.password").doesNotExist());

    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        var body = """
                {
                    "email": "not-email",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUserById() throws Exception {
        var body = """
                {
                    "email": "test@test.com",
                    "password": "123"
                }
                """;

        var result =mockMvc.perform(post("/api/users")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(body))
                .andReturn();

        var id = userRepository.findByEmail("test@test.com").get().getId();

        mockMvc.perform(get("/api/users/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    public void testUpdateUser() throws Exception {
        var createBody = """
                {
                    "email": "test@test.com",
                    "password": "123"
                }
                """;

        var result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
                .andReturn();

        var id = userRepository.findByEmail("test@test.com").get().getId();

        var updateBody = """
                {
                    "email": "updated@test.com"
                }
                """;

        mockMvc.perform(put("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        var body = """
                {
                    "email": "test@test.com",
                    "password": "123"
                }
                """;

        var result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andReturn();

        var id = userRepository.findByEmail("test@test.com").get().getId();

        mockMvc.perform(delete("/api/users/" + id))
                .andExpect(status().isNoContent());
    }
}
