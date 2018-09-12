package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {

    @NonNull
    private String content;

    @NonNull
    private String authorUsername;
}
