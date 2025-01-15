package com.chatr.messages;

import com.chatr.chat.domain.Chat;
import com.chatr.messages.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {


    Page<Message> findByChat(Chat chat, Pageable pageable);


}
