package com.chatr.jwt;

public interface JwtService {


    /**
     *
     * Извлечение email из токена
     *
     * @param token  токен
     * @return email
     */
    public String getEmail(String token, TokenType tokenType);


    /**
     *
     * Генерация токена
     *
     * @param email email пользователя
     * @return token
     */
    public String generateToken(String email, TokenType tokenType);

    /**
     *
     * валидация токена
     *
     * @return true если токен валиден
     */
    public boolean validateToken(String token, TokenType tokenType);


}
