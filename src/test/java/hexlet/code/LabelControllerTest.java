package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.model.Label;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class LabelControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

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
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();

        var user = new User();
        user.setEmail("test@test.com");
        user.setPasswordDigest("password");
        userRepository.save(user);
        token = jwtUtils.generateToken("test@test.com");

        testLabel = new Label();
        testLabel.setName("Test label");
        labelRepository.save(testLabel);
    }

    @Test
    public void testGetAllLabels() throws Exception {
        var result = mockMvc.perform(get("/api/labels")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var labels = objectMapper.readValue(body, new TypeReference<List<LabelDTO>>() { });
        var labelsFromDb = labelRepository.findAll();

        assertThat(labels).hasSize(labelsFromDb.size());

        var labelsFromResponse = labels.stream()
                .map(LabelDTO::getName)
                .toList();
        var labelFromDb = labelsFromDb.stream()
                .map(Label::getName)
                .toList();

        assertThat(labelsFromResponse).containsExactlyInAnyOrderElementsOf(labelFromDb);
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
                .andExpect(status().isCreated());

        var createdLabel = labelRepository.findByName("New label");
        assertThat(createdLabel).isPresent();
        assertThat(createdLabel.get().getName()).isEqualTo("New label");
    }

    @Test
    public void testGetLabelById() throws Exception {
        var result = mockMvc.perform(get("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var labelDTO = objectMapper.readValue(body, LabelDTO.class);

        assertThat(labelDTO.getName()).isEqualTo(testLabel.getName());
        assertThat(labelDTO.getId()).isEqualTo(testLabel.getId());
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
                .andExpect(status().isOk());

        var updatedLabel = labelRepository.findById(testLabel.getId()).get();
        assertThat(updatedLabel.getName()).isEqualTo("Updated label");
    }

    @Test
    public void testDeleteLabel() throws Exception {
        mockMvc.perform(delete("/api/labels/" + testLabel.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(labelRepository.findById(testLabel.getId())).isEmpty();
    }
}
