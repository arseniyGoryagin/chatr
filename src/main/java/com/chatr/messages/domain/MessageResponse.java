package com.chatr.messages.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {
    private String content;
    private Long chatId;
}
