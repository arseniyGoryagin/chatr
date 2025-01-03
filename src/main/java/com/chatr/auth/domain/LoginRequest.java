package com.chatr.auth.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Schema(description = "Запрос аутентификации")
public class LoginRequest {

    @NotBlank
    @Email
    @Schema(description = "Email пользователя", example = "example@email.com")
    private String email;

    @NotBlank
    @Schema(description = "Пароль пользователя", example = "password")
    @Size(min = 8, max = 255, message = "Длина пароля должна быть не мения 8 символов и не более 255 символов")
    private String password;

}