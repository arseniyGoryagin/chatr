package com.chatr.passwordreset;


import com.chatr.auth.domain.MessageResponse;
import com.chatr.exceptions.ErrorMessageResponse;
import com.chatr.exceptions.InvalidResetCodeException;
import com.chatr.exceptions.InvalidResetCodeTokenException;
import com.chatr.passwordreset.domain.PasswordChangeTokenRequest;
import com.chatr.passwordreset.domain.PasswordChangeTokenResponse;
import com.chatr.passwordreset.domain.PasswordResetRequest;
import com.chatr.passwordreset.domain.PasswordVerifyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password-recovery")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;


    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody PasswordResetRequest request){
        passwordResetService.resetPassword(request.getEmail());
        return new ResponseEntity<>(new MessageResponse("Письмо с востановлением пароля отправленно на почту"), HttpStatus.OK);
    }


    @PostMapping("/verify-code")
    public ResponseEntity<PasswordChangeTokenResponse> verifyCode(@Valid @RequestBody PasswordVerifyRequest request){
        String token = passwordResetService.verifyPasswordResetCode(request.getEmail(), request.getCode());
        return new ResponseEntity<>(new PasswordChangeTokenResponse(token), HttpStatus.OK);
    }


    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody PasswordChangeTokenRequest request){
        passwordResetService.changePassword(request.getPassword(), request.getToken());
        return new ResponseEntity<>(new MessageResponse("Пароль успешно изменён"), HttpStatus.OK);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageResponse> handleException(MailAuthenticationException e){
        return new ResponseEntity<ErrorMessageResponse>(new ErrorMessageResponse(e.getLocalizedMessage(), e.getCause().getLocalizedMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(InvalidResetCodeException.class)
    public ResponseEntity<ErrorMessageResponse> handleInvalidResetCodeException (InvalidResetCodeException e){
        return new ResponseEntity<>(new ErrorMessageResponse(e.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }


    @ExceptionHandler(InvalidResetCodeTokenException.class)
    public ResponseEntity<ErrorMessageResponse> handleInvalidResetCodeTokenException (InvalidResetCodeTokenException e){
        return new ResponseEntity<>(new ErrorMessageResponse(e.getLocalizedMessage()), HttpStatus.UNAUTHORIZED);
    }



}
