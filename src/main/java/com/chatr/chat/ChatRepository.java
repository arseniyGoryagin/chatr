package com.chatr.chat;

import com.chatr.chat.domain.Chat;
import com.chatr.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
Page<Chat> findByParticipantsContaining(User user, Pageable pageable);
}
