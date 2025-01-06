package com.chatr;


import com.chatr.email.EmailService;
import com.chatr.email.domain.Email;
import com.chatr.exceptions.InvalidRefreshTokenException;
import com.chatr.exceptions.InvalidResetCodeException;
import com.chatr.jwt.JwtService;
import com.chatr.jwt.TokenType;
import com.chatr.passwordreset.PasswordResetRepository;
import com.chatr.passwordreset.PasswordResetService;
import com.chatr.passwordreset.PasswordResetToken;
import com.chatr.passwordreset.codegenerator.CodeGenerator;
import com.chatr.passwordreset.timeprovider.TimeProvider;
import com.chatr.user.UserService;
import com.chatr.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PasswordResetServiceTest {


    @InjectMocks
    private PasswordResetService passwordResetService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CodeGenerator codeGenerator;

    @Mock
    private UserService userService;

    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private PasswordEncoder passwordEncoder;


    @Test
    public void test_reset_password_user_email_correct(){

        String email  ="email@email.com";
        String code = "12345";
        LocalDateTime currentTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        User user = new User();
        user.setEmail(email);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setEmail(user.getEmail());
        passwordResetToken.setCode(code);
        passwordResetToken.setExpirationTime(currentTime.plusMinutes(30));

        Email emailToSend = Email.builder()
                .address(email)
                .body("Код для востановления " + code)
                .subject("Востановление пароля")
                .build();

        when(userService.getUserByEmail(email)).thenReturn(Optional.of(user));
        when(codeGenerator.generateCode()).thenReturn(code);
        when(timeProvider.getCurrentTime()).thenReturn(currentTime);



        passwordResetService.resetPassword(email);


        verify(userService, times(1)).getUserByEmail(email);
        verify(passwordResetRepository, times(1)).save(passwordResetToken);
        verify(emailService, times(1)).sendEmail(emailToSend);



    }


    @Test
    public void test_reset_password_user_email_incorrect(){

        String email  ="email@email.com";

        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            passwordResetService.resetPassword(email);
        });


    }

    @Test
    public void test_verify_code_code_correct_non_expired(){

        String code  ="12345";
        String email = "example@email.com";

        LocalDateTime currentTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        PasswordResetToken passwordResetToken =new PasswordResetToken();
        passwordResetToken.setCode(code);
        passwordResetToken.setEmail(email);
        passwordResetToken.setExpirationTime(currentTime.plusMinutes(5));

        when(passwordResetRepository.findByEmailAndCode(email, code)).thenReturn(Optional.of(passwordResetToken));
        when(timeProvider.getCurrentTime()).thenReturn(currentTime);

        passwordResetService.verifyPasswordResetCode(email, code);

        verify(passwordResetRepository, times(1)).findByEmailAndCode(email, code);
        verify(passwordResetRepository, times(1)).delete(passwordResetToken);
        verify(jwtService, times(1)).generateToken(email, TokenType.RESETPASSWORD);
    }

    @Test
    public void test_verify_code_code_incorrect(){

        String code  ="12345";
        String email = "example@email.com";

        when(passwordResetRepository.findByEmailAndCode(email, code)).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidResetCodeException.class, () -> {
            passwordResetService.verifyPasswordResetCode(email, code);
        });

        verify(passwordResetRepository, times(1)).findByEmailAndCode(email, code);

    }

    @Test
    public void test_verify_code_code_expired(){

        String code  ="12345";
        String email = "example@email.com";

        LocalDateTime currentTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);

        PasswordResetToken passwordResetToken =new PasswordResetToken();
        passwordResetToken.setCode(code);
        passwordResetToken.setEmail(email);
        passwordResetToken.setExpirationTime(currentTime.minusMinutes(5));

        when(passwordResetRepository.findByEmailAndCode(email, code)).thenReturn(Optional.of(passwordResetToken));
        when(timeProvider.getCurrentTime()).thenReturn(currentTime);

        Assertions.assertThrows(InvalidResetCodeException.class, () -> {
            passwordResetService.verifyPasswordResetCode(email, code);
        });

        verify(passwordResetRepository, times(1)).delete(passwordResetToken);

    }


    @Test
    public void test_change_password_correct_user(){

        String token  ="token";
        String username = "username";
        String newPassword= "12345678";
        String newPasswordEncoded = "12345678Encoded";


        User user = new User();
        user.setEmail(username);
        user.setPassword("oldpassword");


        when(jwtService.getUsername(token, TokenType.RESETPASSWORD)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn(newPasswordEncoded);

        passwordResetService.changePassword( newPassword,token);

        Assertions.assertEquals(newPasswordEncoded, user.getPassword());
        verify(jwtService, times(1)).getUsername(token, TokenType.RESETPASSWORD);
        verify(userService, times(1)).getUserByUsername(username);
        verify(userService, times(1)).saveUser(user);


    }


    @Test
    public void test_change_password_incorrect_user(){

        String token  ="token";
        String username = "username";
        String newPassword= "12345678";

        when(jwtService.getUsername(token, TokenType.RESETPASSWORD)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidRefreshTokenException.class, () -> {
            passwordResetService.changePassword(newPassword, token);
        });

        verify(userService, times(1)).getUserByUsername(username);



    }

    // TODO test exception when token not valid from jwt service



}
