package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    private JsonNullable<String> firstName = JsonNullable.undefined();
    private JsonNullable<String> lastName = JsonNullable.undefined();

    @Email(message = "некорректный формат email!")
    private JsonNullable<String> email = JsonNullable.undefined();

    @Size(min = 3, message = "пароль минимум 3 символа!")
    private JsonNullable<String> password = JsonNullable.undefined();
}
