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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password-recovery")
@Tag(name = "Востановление пароля")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(summary = "Забыл прароль")
    @ApiResponse(responseCode = "200", description = "Обновление токена успешно", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    @ApiResponse(responseCode = "404", description = "Нет пользователя с такой почтой", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody PasswordResetRequest request){
        passwordResetService.resetPassword(request.getEmail());
        return new ResponseEntity<>(new MessageResponse("Письмо с востановлением пароля отправленно на почту"), HttpStatus.OK);
    }


    @Operation(summary = "Проверка валидности кода востановления пароля")
    @ApiResponse(responseCode = "200", description = "Валидация прошла успешно", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PasswordChangeTokenResponse.class)))
    @ApiResponse(responseCode = "401", description = "Не правильный код", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    @PostMapping("/verify-code")
    public ResponseEntity<PasswordChangeTokenResponse> verifyCode(@Valid @RequestBody PasswordVerifyRequest request){
        String token = passwordResetService.verifyPasswordResetCode(request.getEmail(), request.getCode());
        return new ResponseEntity<>(new PasswordChangeTokenResponse(token), HttpStatus.OK);
    }


    @Operation(summary = "Измена пароля по токену измены пароля")
    @ApiResponse(responseCode = "200", description = "Пароль успешно изменён", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class)))
    @ApiResponse(responseCode = "401", description = "Неправильный токен", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
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