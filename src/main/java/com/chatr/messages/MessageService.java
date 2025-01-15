package com.chatr.messages;


import com.chatr.chat.ChatRepository;
import com.chatr.chat.ChatService;
import com.chatr.chat.domain.Chat;
import com.chatr.kafka.KafkaTopics;
import com.chatr.messages.domain.Message;
import com.chatr.messages.domain.MessageResponse;
import com.chatr.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MessageService {


    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;


    private MessageResponse toMessageResponse(Message message){
        return  MessageResponse.builder()
                .content(message.getContent())
                .chatId(message.getChat().getId())
                .build();
    }



    @KafkaListener(topics = KafkaTopics.MESSAGE_SENT, groupId = "group_id")
    void messageSent(Message message){
        messageRepository.save(message);
    }


    Page<MessageResponse> getMessages(UserDetails userDetails,  Long chatId, int page, int size){
        Chat chat = chatRepository.findById(chatId).orElseThrow(()-> {throw new NoSuchElementException("No such chat");});
        if(!chat.getParticipants().contains((User) userDetails)){
            throw new NoSuchElementException("No such chat");
        }
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByChat(chat, pageable).map(this::toMessageResponse);
    }




}
