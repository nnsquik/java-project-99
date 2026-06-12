package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String token;
    private User testUser;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        taskRepository.deleteAll();
        labelRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPasswordDigest("qwerty");
        userRepository.save(testUser);

        token = jwtUtils.generateToken(testUser.getEmail());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        var result = mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var users = objectMapper.readValue(body, new TypeReference<List<UserDTO>>() { });
        var usersFromDb = userRepository.findAll();

        assertThat(users).hasSize(usersFromDb.size());

        var emailFromResponse = users.stream()
                .map(UserDTO::getEmail)
                .toList();
        var emailsFromDb = usersFromDb.stream()
                .map(User::getEmail)
                .toList();

        assertThat(emailFromResponse).containsExactlyInAnyOrderElementsOf(emailsFromDb);
    }

    @Test
    public void testCreateUser() throws Exception {
        var body = """
                {
                    "email": "new@test.com",
                    "password": "123"
                }
                """;

        var result = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andReturn();

        var createdUser = userRepository.findByEmail("new@test.com");
        assertThat(createdUser).isPresent();
        assertThat(createdUser.get().getEmail()).isEqualTo("new@test.com");

        var responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).doesNotContain("password");
    }

    @Test
    public void testCreateUserWithInvalidEmail() throws Exception {
        var body = """
                {
                    "email": "not-an-email",
                    "password": "123"
                }
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

        assertThat(userRepository.findByEmail("not-an-email")).isEmpty();
    }

    @Test
    public void testGetUserById() throws Exception {
        var result = mockMvc.perform(get("/api/users/" + testUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var userDTO = objectMapper.readValue(body, UserDTO.class);

        assertThat(userDTO.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(userDTO.getId()).isEqualTo(testUser.getId());
    }

    @Test
    public void testUpdateUser() throws Exception {
        var id = userRepository.findByEmail("test@test.com").get().getId();

        var updateBody = """
                {
                    "email": "updated@test.com"
                }
                """;

        mockMvc.perform(put("/api/users/" + testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"));

        var updatedUser = userRepository.findById(testUser.getId()).get();
        assertThat(updatedUser.getEmail()).isEqualTo("updated@test.com");
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/api/users/" + testUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
