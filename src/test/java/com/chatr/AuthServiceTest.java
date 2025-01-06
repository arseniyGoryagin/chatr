package com.chatr;


import com.chatr.auth.AuthService;
import com.chatr.exceptions.InvalidRefreshTokenException;
import com.chatr.jwt.JwtService;
import com.chatr.jwt.TokenType;
import com.chatr.user.UserService;
import com.chatr.user.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;


    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testRegisterUser(){

        String password = "password";
        String encodedPassword = "encodedPassword";
        String jwtToken = "jwtToken";
        String refreshToken = "refreshToken";

        User user  = new User();

        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userService.createNewUser(user)).thenReturn(user);
        when(jwtService.generateToken(user.getEmail(), TokenType.ACCESS)).thenReturn(jwtToken);
        when(jwtService.generateToken(user.getEmail(), TokenType.REFRESH)).thenReturn(refreshToken);

        var result = authService.registerUser(user);


        verify(userService, times(1)).createNewUser(user);
        Assertions.assertEquals(jwtToken, result.getToken());
        Assertions.assertEquals(refreshToken, result.getRefreshToken());

    }


    @Test
    void testAuthUser(){

        String email  = "newuser@email.com";
        String password = "password";
        String jwtToken = "jwtToken";
        String refreshToken = "refreshToken";

        when(jwtService.generateToken(email, TokenType.ACCESS)).thenReturn(jwtToken);
        when(jwtService.generateToken(email, TokenType.REFRESH)).thenReturn(refreshToken);

        var result = authService.authUser(email, password);

        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(email, password));
        Assertions.assertEquals(jwtToken, result.getToken());
        Assertions.assertEquals(refreshToken, result.getRefreshToken());

    }


    @Test
    void test_refresh_token_valid(){


        String jwtToken = "jwtToken";
        String username = "username";
        String refreshToken = "refreshToken";

        when(jwtService.generateToken(username, TokenType.ACCESS)).thenReturn(jwtToken);
        when(jwtService.validateToken(refreshToken, TokenType.REFRESH)).thenReturn(true);
        when(jwtService.getUsername(refreshToken, TokenType.REFRESH)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Optional.of(new User()));

       var result = authService.refreshToken(refreshToken);

       verify(userService, times(1)).getUserByUsername(username);
       Assertions.assertEquals(jwtToken, result.getToken());
       Assertions.assertEquals(refreshToken, result.getRefreshToken());


    }


    @Test
    void test_refresh_token_invalid_jwt_signature(){

        String username = "username";
        String refreshToken = "refreshToken";


        when(jwtService.validateToken(refreshToken, TokenType.REFRESH)).thenReturn(false);
        when(jwtService.getUsername(refreshToken, TokenType.REFRESH)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Optional.of(new User()));

        Assertions.assertThrows(InvalidRefreshTokenException.class, () -> {
            authService.refreshToken(refreshToken);
        });

        verify(userService, times(1)).getUserByUsername(username);

    }

    @Test
    void testRefreshToken_invalid_email_in_jwt(){

        String username = "username";
        String refreshToken = "refreshToken";


        when(jwtService.getUsername(refreshToken, TokenType.REFRESH)).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(Optional.empty());

        Assertions.assertThrows(InvalidRefreshTokenException.class, () -> {
            authService.refreshToken(refreshToken);
        });

        verify(userService, times(1)).getUserByUsername(username);

    }



}
