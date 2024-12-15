package com.chatr.passwordreset.domain;



import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeTokenRequest {

    @Size(min = 8, max = 255, message = "Длина пароля должна быть не мения 8 символов и не более 255 символов")
    String password;


    String token;


}
