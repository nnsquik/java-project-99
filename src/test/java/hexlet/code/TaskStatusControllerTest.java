package hexlet.code;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.JWTUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        private JWTUtils jwtUtils;

        private String token;
        private MockMvc mockMvc;

        @BeforeEach
        public void setUp() {
            mockMvc = MockMvcBuilders
                    .webAppContextSetup(context)
                    .apply(SecurityMockMvcConfigurers.springSecurity())
                    .build();

            userRepository.deleteAll();
            taskStatusRepository.deleteAll();

            var user = new User();
            user.setEmail("test@test.com");
            user.setPasswordDigest("qwerty");
            userRepository.save(user);

            var taskStatus = new TaskStatus();
            taskStatus.setName("New");
            taskStatus.setSlug("new");
            taskStatusRepository.save(taskStatus);

            token = jwtUtils.generateToken(user.getEmail());
        }

        @Test
        public void testGetAllStatuses() throws Exception {
            mockMvc.perform(get("/api/task_statuses")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
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
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Test"))
                    .andExpect(jsonPath("$.slug").value("test"));
        }

        @Test
        public void testGetStatusById() throws Exception {
            var id = taskStatusRepository.findBySlug("new").get().getId();

            mockMvc.perform(get("/api/task_statuses/" + id)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("New"));
        }

        @Test
        public void testUpdateStatus() throws Exception {
            var id = taskStatusRepository.findBySlug("new").get().getId();

            var updateBody = """
                    {
                        "name": "Updated"
                    }
                    """;

            mockMvc.perform(put("/api/task_statuses/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(updateBody)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Updated"));
        }

        @Test
        public void testDeleteStatus() throws Exception {
            var id = taskStatusRepository.findBySlug("new").get().getId();

            mockMvc.perform(delete("/api/task_statuses/" + id)
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isNoContent());
        }
    }
