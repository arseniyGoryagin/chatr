package com.chatr.auth.domain;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RefreshTokenRequest {

    @JsonProperty("refresh_token")
    String refreshToken;

}
