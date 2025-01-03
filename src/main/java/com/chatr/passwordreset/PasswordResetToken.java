package com.chatr.passwordreset;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @Column(name = "email", updatable = false, nullable = false)
    private String email;


    @Column(name = "code", updatable = false, nullable = false)
    private String code;

    @Column(name = "expiration_time", updatable = false, nullable = false)
    private LocalDateTime expirationTime;

}
