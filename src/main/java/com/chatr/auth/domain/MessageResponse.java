package com.chatr.auth.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Schema(description = "Ответ одной строкой")
@AllArgsConstructor
public class MessageResponse {
    @Schema(description = "ответ строкой", example = "сброс пароля отправлен на почту")
    private String message ;

}