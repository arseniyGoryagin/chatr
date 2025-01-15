package com.chatr.messages.domain;


import com.chatr.chat.domain.Chat;
import com.chatr.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
@Builder
@Entity
@Table
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Chat chat;

    @ManyToOne
    private User from;

    private String content;

}
