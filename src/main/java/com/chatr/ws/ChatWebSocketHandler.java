package com.chatr.ws;

import com.chatr.chat.ChatService;
import com.chatr.chat.domain.Chat;
import com.chatr.kafka.KafkaTopics;
import com.chatr.messages.MessageService;
import com.chatr.messages.domain.Message;
import com.chatr.messages.domain.MessageResponse;
import com.chatr.user.domain.User;
import com.chatr.ws.domain.WsChatMessage;
import com.chatr.ws.exceptions.NotAllowedToSendMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.*;



@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final KafkaTemplate<String, Message>  messageKafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    private final Map<String, WebSocketSession> sessions = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        sessions.put(username, session);
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws IOException {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String payload = textMessage.getPayload();
        WsChatMessage wsChatMessage = objectMapper.readValue(payload, WsChatMessage.class);

        Chat chat = chatService.getChat(wsChatMessage.getChatId());

        Set<User> users = chat.getParticipants();

        if(!users.contains(currentUser)){
            throw new NotAllowedToSendMessage("The user cant send message to this chat");
        }

        Message message = Message.builder()
                .from(currentUser)
                .chat(chat)
                .content(wsChatMessage.getMessage())
                .build();

        messageKafkaTemplate.send(KafkaTopics.MESSAGE_SENT, message);

        for(User user : users){
            if(!user.equals(currentUser)){
                WebSocketSession socketSession = sessions.get(user.getUsername());
                if(socketSession.isOpen()){
                    socketSession.sendMessage(new TextMessage(wsChatMessage.getMessage()));
                }else{
                    sessions.remove(user.getUsername());
                }
            }

        }

    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus){
        sessions.remove(session);
    }

}
