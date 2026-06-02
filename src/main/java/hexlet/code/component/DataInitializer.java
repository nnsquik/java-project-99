package hexlet.code.component;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskStatusRepository taskStatusRepository;

    @Value("${admin.password:qwerty}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("hexlet@example.com");
            admin.setPasswordDigest(passwordEncoder.encode(adminPassword));
            userRepository.save(admin);
        }

        var defaultStatutes = List.of(
                new String[]{"Draft", "draft"},
                new String[]{"ToReview", "to_review"},
                new String[]{"ToBeFixed", "to_be_fixed"},
                new String[]{"ToPublish", "to_publish"},
                new String[]{"Published", "published"}
        );

        for (var status :defaultStatutes) {
            if (taskStatusRepository.findBySlug(status[1]).isEmpty()) {
                var taskStatus = new TaskStatus();
                taskStatus.setName(status[0]);
                taskStatus.setSlug(status[1]);
                taskStatusRepository.save(taskStatus);
            }
        }
    }
}
