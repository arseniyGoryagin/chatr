package com.chatr.passwordreset;


import com.chatr.email.EmailService;
import com.chatr.email.domain.Email;
import com.chatr.exceptions.InvalidRefreshTokenException;
import com.chatr.exceptions.InvalidResetCodeException;
import com.chatr.jwt.JwtService;
import com.chatr.jwt.TokenType;
import com.chatr.passwordreset.codegenerator.CodeGenerator;
import com.chatr.passwordreset.timeprovider.TimeProvider;
import com.chatr.user.UserService;
import com.chatr.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordResetService {

    private final EmailService emailService;
    private final UserService userService;
    private final PasswordResetRepository passwordResetRepository;
    private final JwtService jwtService;
    private final CodeGenerator codeGenerator;
    private final TimeProvider timeProvider;
    private final PasswordEncoder passwordEncoder;





    /**
     * Востановление пароля пользователя
     *
     *
     * @param email email пользователя
     */
    public void resetPassword(String email){


        User user = userService.getByUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No such user"));

        LocalDateTime expTime = timeProvider.getCurrentTime().plusMinutes(30);

        String code = codeGenerator.generateCode();

        PasswordResetToken passwordResetToken = PasswordResetToken
                .builder()
                .code(code)
                .email(user.getEmail())
                .expirationTime(expTime)
                .build();

        passwordResetRepository.save(passwordResetToken);


        Email resetPasswordEmail = Email
                .builder()
                .address(email)
                .body("Код для востановления " + code)
                .subject("Востановление пароля")
                .build();


        emailService.sendEmail(resetPasswordEmail);


    }


    /**
     * Проверка валидности кода востановления
     *
     *
     * @param email email пользователя
     * @param code  код востановления
     *
     * @return token для смены пароля
     */
    public String verifyPasswordResetCode(String email, String code){

            PasswordResetToken passwordResetToken = passwordResetRepository.findByEmailAndCode(email, code)
                    .orElseThrow(() -> new InvalidResetCodeException("The code is incorrect"));


            LocalDateTime currentTime = timeProvider.getCurrentTime();

            if(currentTime.isAfter(passwordResetToken.getExpirationTime())){
                passwordResetRepository.delete(passwordResetToken);
                throw  new InvalidResetCodeException("The code is expired");
            }


            passwordResetRepository.delete(passwordResetToken);


            return jwtService.generateToken(email, TokenType.RESETPASSWORD);


    }



    /**
     * Измена пароля пользователя
     *
     *
     * @param newPassword новый пароль пользователя
     * @param token  код востановления
     *
     *
     */
    public void changePassword(String newPassword, String token){

        String email = jwtService.getEmail(token, TokenType.RESETPASSWORD);

        User user = userService.getByUserByEmail(email).orElseThrow(() -> new InvalidRefreshTokenException("No such user for this token exists"));

        user.setPassword(passwordEncoder.encode(newPassword));

        userService.saveUser(user);

    }



}
