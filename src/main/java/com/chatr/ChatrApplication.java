package com.chatr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.chatr")
public class ChatrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatrApplication.class, args);
	}

}
