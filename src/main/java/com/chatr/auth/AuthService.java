package com.chatr.auth;


import com.chatr.auth.domain.TokenResponse;
import com.chatr.exceptions.InvalidRefreshTokenException;
import com.chatr.jwt.JwtService;
import com.chatr.jwt.TokenType;
import com.chatr.kafka.KafkaTopics;
import com.chatr.user.UserService;
import com.chatr.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    /**
     * Регистрация пользователя
     *
     *
     * @param user User
     * @return токен
     */
    public TokenResponse registerUser (User user){

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.createNewUser(user);

        kafkaTemplate.send(KafkaTopics.USER_REGISTERED, user.getEmail());

        return TokenResponse.builder()
                .token(jwtService.generateToken(user.getUsername(), TokenType.ACCESS))
                .refreshToken(jwtService.generateToken(user.getUsername(), TokenType.REFRESH))
                .build();
    }




    /**
     * Аутентификация пользователя
     *
     * @param username email пользователя
     * @param password пароль пользователя
     * @return токен
     */
    public TokenResponse authUser (String username, String password){

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                username,
                password
        ));

        return TokenResponse.builder()
                .token(jwtService.generateToken(username, TokenType.ACCESS))
                .refreshToken(jwtService.generateToken(username, TokenType.REFRESH))
                .build();
    }


    public TokenResponse refreshToken(String refreshToken){

            String username = jwtService.getUsername(refreshToken, TokenType.REFRESH);


            userService.getUserByUsername(username)
                    .orElseThrow(() -> new InvalidRefreshTokenException("Нет пользователя с таким токеном"));

            // TODO add valid by email
            if(jwtService.validateToken(refreshToken,TokenType.REFRESH)){

                var newToken = jwtService.generateToken(username, TokenType.ACCESS);

                return TokenResponse.builder()
                        .token(newToken)
                        .refreshToken(refreshToken)
                        .build();

            }

            throw new InvalidRefreshTokenException("Невалидный refresh token");

    }



}
