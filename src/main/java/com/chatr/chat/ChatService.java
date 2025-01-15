package com.chatr.chat;


import com.chatr.chat.domain.Chat;
import com.chatr.chat.exceptions.NoSuchChatException;
import com.chatr.user.UserService;
import com.chatr.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserService userService;

    public Chat makeNewChat(List<String> withUsernames){

        Set<User> participants = new HashSet<>();
        for(String username : withUsernames){
            User user = userService.getUserByUsername(username).orElseThrow(() -> {throw  new UsernameNotFoundException("No such username");});
            participants.add(user);
        }

        Chat chat = Chat.builder()
                .participants(participants)
                .build();

        return chatRepository.save(chat);

    }

    public Chat getChat(Long chatId){
        return chatRepository.findById(chatId).orElseThrow(() -> {throw new NoSuchChatException("No such chat");});
    }

    public Page<Chat> getUserChats(User currentUser, int page, int size){
        return chatRepository.findByParticipantsContaining(currentUser, PageRequest.of(page, size));
    }


}
