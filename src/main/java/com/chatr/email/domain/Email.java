package com.chatr.email.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class Email {

    private String address;
    private String subject;
    private String body;
}
