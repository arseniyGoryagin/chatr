package com.chatr.auth;


import com.chatr.auth.domain.*;
import com.chatr.exceptions.ErrorMessageResponse;
import com.chatr.exceptions.InvalidRefreshTokenException;
import com.chatr.exceptions.UserAlreadyRegisteredException;
import com.chatr.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;



    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }


    @PostMapping("/register")
    public TokenResponse register(@RequestBody @Valid RegisterRequest request){
        var user = User
                .builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .build();
        return authService.registerUser(user);
    }


    @PostMapping("/login")
    public TokenResponse auth(@RequestBody @Valid LoginRequest request){
        return authService.authUser(request.getUsername(), request.getPassword());
    }


    @PostMapping("/refresh-token")
    public TokenResponse refreshToken(@RequestBody @Valid RefreshTokenRequest request){
        return authService.refreshToken(request.getRefreshToken());
    }



    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorMessageResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException e){
        return new ResponseEntity<>(new ErrorMessageResponse(e.getLocalizedMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    public ResponseEntity<ErrorMessageResponse> handleAlreadyRegisteredException(UserAlreadyRegisteredException e){
        return new ResponseEntity<>(new ErrorMessageResponse(e.getLocalizedMessage()), HttpStatus.CONFLICT);
    }


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessageResponse> handleBadCredentialException (BadCredentialsException e){
        return new ResponseEntity<>(new ErrorMessageResponse("Пароль или почта введены неверно"), HttpStatus.FORBIDDEN);
    }


}
