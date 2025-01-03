package com.chatr.passwordreset.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
@Schema(description = "Запрос на востановленгие пароля")
public class PasswordResetRequest {

    @Schema(description = "email пользователя", example = "example@email.com")
    @Email(message = "email должен быть валиден")
    private String email ;

}