package com.chatr.passwordreset.domain;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordChangeTokenResponse {


    String token;
}
