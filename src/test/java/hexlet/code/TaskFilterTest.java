package hexlet.code;

import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
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
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TaskFilterTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTUtils jwtUtils;

    private MockMvc mockMvc;
    private String token;
    private Task testTask;
    private User testUser;
    private TaskStatus testStatus;
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

        testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPasswordDigest("password");
        userRepository.save(testUser);
        token = jwtUtils.generateToken(testUser.getEmail());

        testStatus = new TaskStatus();
        testStatus.setName("Draft");
        testStatus.setSlug("draft");
        taskStatusRepository.save(testStatus);

        testLabel = new Label();
        testLabel.setName("feature");
        labelRepository.save(testLabel);

        testTask = new Task();
        testTask.setName("Create new feature");
        testTask.setDescription("Test content");
        testTask.setTaskStatus(testStatus);
        testTask.setAssignee(testUser);
        testTask.setLabels(new java.util.HashSet<>(java.util.List.of(testLabel)));
        taskRepository.save(testTask);
    }

    @Test
    public void testFilterByTitle() throws Exception {
        mockMvc.perform(get("/api/tasks?titleCont=create")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Create new feature"));
    }

    @Test
    public void testFilterByAssigneeId() throws Exception {
        mockMvc.perform(get("/api/tasks?assigneeId=" + testUser.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Create new feature"));
    }

    @Test
    public void testFilterByStatus() throws Exception {
        mockMvc.perform(get("/api/tasks?status=draft")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Create new feature"));
    }

    @Test
    public void testFilterByLabelId() throws Exception {
        mockMvc.perform(get("/api/tasks?labelId=" + testLabel.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Create new feature"));
    }

    @Test
    public void testFilterNoResults() throws Exception {
        mockMvc.perform(get("/api/tasks?titleCont=nonexistent")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}