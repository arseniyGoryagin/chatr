package com.chatr.passwordreset.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordVerifyRequest {


    @Email
    String email;


    @Size(max = 5, min = 5)
    String code;


}
