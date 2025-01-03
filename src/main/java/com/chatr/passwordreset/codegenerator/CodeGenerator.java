package com.chatr.passwordreset.codegenerator;


import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class CodeGenerator {


    /**
     * Генерированите 5 значного пароля
     *
     *
     * @return 5 значиный код для востановления пароля
     */
    public String generateCode(){
        SecureRandom secureRandom = new SecureRandom();
        int code =  10000 + secureRandom.nextInt(9000);
        return String.valueOf(code);
    }
}
