package com.chatr.profile.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileRequest {
    private String username;
    private String description;
}
