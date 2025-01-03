package com.chatr.passwordreset.domain;



import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос измену пароля")
public class PasswordChangeTokenRequest {

    @Schema(description = "Новый пароль пользователя", example = "12345Ar")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть не мения 8 символов и не более 255 символов")
    private String password;


    @Schema(description = "Токен влстановления пароля", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    private String token;
}