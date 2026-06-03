package hexlet.code;

import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    private MockMvc mockMvc;
    private String token;
    private Label testLabel;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        taskRepository.deleteAll();
        labelRepository.deleteAll();

        if (userRepository.findByEmail("test@test.com").isEmpty()) {
            var user = new hexlet.code.model.User();
            user.setEmail("test@test.com");
            user.setPasswordDigest("password");
            userRepository.save(user);
        }
        token = jwtUtils.generateToken("test@test.com");

        var label = new Label();
        label.setName("Test label");
        labelRepository.save(label);
        testLabel = label;
    }

    @Test
    public void testGetAllLabels() throws Exception {
        mockMvc.perform(get("/api/labels")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test label"));
    }

    @Test
    public void testCreateLabel() throws Exception {
        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "New label"
                                }
                                """)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New label"));
    }

    @Test
    public void testGetLabelById() throws Exception {
        mockMvc.perform(get("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test label"));
    }

    @Test
    public void testUpdateLabel() throws Exception {
        mockMvc.perform(put("/api/labels/" + testLabel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Updated label"
                                }
                                """)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated label"));
    }

    @Test
    public void testDeleteLabel() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
