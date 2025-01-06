package com.chatr.auth;


import com.chatr.auth.domain.*;
import com.chatr.exceptions.ErrorMessageResponse;
import com.chatr.exceptions.InvalidRefreshTokenException;
import com.chatr.exceptions.UserAlreadyInUseException;
import com.chatr.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Аутентификация")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "200", description = "Регистрация успешна", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "409", description = "Не правильно в ведённеы данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))@ApiResponse(responseCode = "409", description = "Пользователь с такой почтой уже существует", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    @PostMapping("/register")
    public TokenResponse register(@RequestBody @Valid RegisterRequest request){
        var user = User
                .builder()
                .password(request.getPassword())
                .email(request.getEmail())
                .description("")
                .username(request.getUsername())
                .build();
        return authService.registerUser(user);
    }

    @Operation(summary = "Авторизация пользователя")
    @ApiResponse(responseCode = "200", description = "Авторизация успешна", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "404", description = "Пароль или почты неверны", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    @ApiResponse(responseCode = "409", description = "Не правильно в ведённеы данные", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    @PostMapping("/login")
    public TokenResponse auth(@RequestBody @Valid LoginRequest request){
        return authService.authUser(request.getUsername(), request.getPassword());
    }

    @Operation(summary = "Обновление токена")
    @ApiResponse(responseCode = "200", description = "Обновление токена успешно", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TokenResponse.class)))
    @ApiResponse(responseCode = "403", description = "Невалидный refresh token", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessageResponse.class)))
    @PostMapping("/refresh-token")
    public TokenResponse refreshToken(@RequestBody @Valid RefreshTokenRequest request){
        return authService.refreshToken(request.getRefreshToken());
    }




    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorMessageResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e){
        return new ResponseEntity<>(new ErrorMessageResponse(e.getLocalizedMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyInUseException.class)
    public ResponseEntity<ErrorMessageResponse> handleAlreadyRegisteredException(UserAlreadyInUseException e){
        return new ResponseEntity<>(new ErrorMessageResponse(e.getLocalizedMessage()), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentialException (BadCredentialsException e){
        return new ResponseEntity<>(new ErrorMessageResponse("Пароль или почта введены неверно"), HttpStatus.FORBIDDEN);
    }


}