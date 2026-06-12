package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskDTO;
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
public class TaskControllerTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String token;
    private Task testTask;

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
        user.setPasswordDigest("qwerty");
        userRepository.save(user);

        var status = new TaskStatus();
        status.setName("Draft");
        status.setSlug("draft");
        taskStatusRepository.save(status);

        var task = new Task();
        task.setName("Test task");
        task.setDescription("Test content");
        task.setTaskStatus(status);
        taskRepository.save(task);

        token = jwtUtils.generateToken(user.getEmail());
        testTask = task;
    }

    @Test
    public void testGetAllTasks() throws Exception {
        var result = mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var tasks = objectMapper.readValue(body, new TypeReference<List<TaskDTO>>() { });
        var tasksFromDb = taskRepository.findAll();

        assertThat(tasks).hasSize(tasksFromDb.size());

        var taskFromResponse = tasks.stream()
                .map(TaskDTO::getTitle)
                .toList();
        var taskFromDb = tasksFromDb.stream()
                .map(Task::getName)
                .toList();

        assertThat(taskFromResponse).containsExactlyInAnyOrderElementsOf(taskFromDb);
    }

    @Test
    public void testCreateTask() throws Exception {
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "New task",
                                    "content": "New content",
                                    "status": "draft"
                                }
                                """)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        var createdTask = taskRepository.findAll().stream()
                .filter(t -> t.getName().equals("New task"))
                .findFirst();
        assertThat(createdTask).isPresent();
        assertThat(createdTask.get().getDescription()).isEqualTo("New content");
    }

    @Test
    public void testGetTaskById() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/" + testTask.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var body = result.getResponse().getContentAsString();
        var taskDTO = objectMapper.readValue(body, TaskDTO.class);

        assertThat(taskDTO.getTitle()).isEqualTo(testTask.getName());
        assertThat(taskDTO.getId()).isEqualTo(testTask.getId());
    }

    @Test
    public void testUpdateTask() throws Exception {
        mockMvc.perform(put("/api/tasks/" + testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "title": "Updated task"
                                }
                                """)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        var updatedTask = taskRepository.findById(testTask.getId()).get();
        assertThat(updatedTask.getName()).isEqualTo("Updated task");
        assertThat(updatedTask.getDescription()).isEqualTo("Test content"); // не затёрлось
    }

    @Test
    public void testDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/tasks/" + testTask.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.findById(testTask.getId())).isEmpty();
    }
}
