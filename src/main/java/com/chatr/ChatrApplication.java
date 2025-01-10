package com.chatr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ChatrApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatrApplication.class, args);
	}

}
