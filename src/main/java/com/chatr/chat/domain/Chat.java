package com.chatr.chat.domain;


import com.chatr.user.domain.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Builder
public class Chat {

    @Id
    private Long id;

    @ManyToMany
    private Set<User> participants = new HashSet<>();

}
