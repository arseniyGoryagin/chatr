package com.chatr.auth.domain;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {


    @NotBlank
    private String username;

    @NotBlank
    @Email
    private String email;


    @NotBlank
    @Size(min = 8, max = 255, message = "Длина пароля должна быть не мения 8 символов и не более 255 символов")
    private String password;


}