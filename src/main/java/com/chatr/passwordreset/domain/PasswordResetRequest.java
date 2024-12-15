package com.chatr.passwordreset.domain;


import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class PasswordResetRequest {


    @Email(message = "email должен быть валиден")
    String email ;

}