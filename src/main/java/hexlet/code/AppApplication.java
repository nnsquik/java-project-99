package hexlet.code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

                        // отключает авто. создание default user Spring Security
@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
@EnableJpaAuditing
public class AppApplication {
    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
