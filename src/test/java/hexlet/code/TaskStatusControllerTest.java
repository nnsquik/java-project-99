package hexlet.code;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.status.TaskStatusDTO;
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
    public class TaskStatusControllerTest {

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

        private String token;
        private MockMvc mockMvc;

        private TaskStatus testStatus;

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
            token = jwtUtils.generateToken(user.getEmail());

            testStatus = new TaskStatus();
            testStatus.setName("New");
            testStatus.setSlug("new");
            taskStatusRepository.save(testStatus);
        }

        @Test
        public void testGetAllStatuses() throws Exception {
            var result = mockMvc.perform(get("/api/task_statuses")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

            var body = result.getResponse().getContentAsString();
            var statuses = objectMapper.readValue(body, new TypeReference<List<TaskStatusDTO>>() { });
            var statusesFromDb = taskStatusRepository.findAll();

            assertThat(statuses).hasSize(statusesFromDb.size());

            var statusFromResponse = statuses.stream()
                    .map(TaskStatusDTO::getSlug)
                    .toList();
            var statusFromDb = statusesFromDb.stream()
                    .map(TaskStatus::getSlug)
                    .toList();

            assertThat(statusFromResponse).containsExactlyInAnyOrderElementsOf(statusFromDb);
        }

        @Test
        public void testCreateStatus() throws Exception {
            var body = """
                {
                    "name": "Test",
                    "slug": "test"
                }
                """;

            mockMvc.perform(post("/api/task_statuses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isCreated());

            var createdStatus = taskStatusRepository.findBySlug("test");
            assertThat(createdStatus).isPresent();
            assertThat(createdStatus.get().getName()).isEqualTo("Test");
        }

        @Test
        public void testGetStatusById() throws Exception {
            var result = mockMvc.perform(get("/api/task_statuses/" + testStatus.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andReturn();

            var body = result.getResponse().getContentAsString();
            var statusDTO = objectMapper.readValue(body, TaskStatusDTO.class);

            assertThat(statusDTO.getName()).isEqualTo(testStatus.getName());
            assertThat(statusDTO.getSlug()).isEqualTo(testStatus.getSlug());
        }

        @Test
        public void testUpdateStatus() throws Exception {
            var updateBody = """
                {
                    "name": "Updated"
                }
                """;

            mockMvc.perform(put("/api/task_statuses/" + testStatus.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateBody)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk());

            var updatedStatus = taskStatusRepository.findById(testStatus.getId()).get();
            assertThat(updatedStatus.getName()).isEqualTo("Updated");
            assertThat(updatedStatus.getSlug()).isEqualTo("new"); // slug не изменился
        }

        @Test
        public void testDeleteStatus() throws Exception {
            mockMvc.perform(delete("/api/task_statuses/" + testStatus.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());

            assertThat(taskStatusRepository.findById(testStatus.getId())).isEmpty();
        }
    }
