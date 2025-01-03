package com.chatr.auth.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Запрос на обновление токена")
public class RefreshTokenRequest {

    @JsonProperty("refresh_token")
    @Schema(description = "refresh токен", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    private String refreshToken;

}
