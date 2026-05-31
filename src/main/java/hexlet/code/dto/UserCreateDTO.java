package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserCreateDTO {
    private JsonNullable<String> firstName = JsonNullable.undefined();
    private JsonNullable<String> lastName = JsonNullable.undefined();

    @NotBlank(message = "email обязателен!")
    @Email(message = "некорректный формат email!")
    private JsonNullable<String> email = JsonNullable.undefined();

    @NotBlank(message = "пароль обязателен!")
    @Size(min = 3, message = "пароль минимум 3 символа!")
    private JsonNullable<String> password = JsonNullable.undefined();
}
