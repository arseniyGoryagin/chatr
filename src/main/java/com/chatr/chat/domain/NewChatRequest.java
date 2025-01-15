package com.chatr.chat.domain;
import lombok.Data;
import java.util.List;

@Data
public class NewChatRequest {
    private List<String> with;
}
