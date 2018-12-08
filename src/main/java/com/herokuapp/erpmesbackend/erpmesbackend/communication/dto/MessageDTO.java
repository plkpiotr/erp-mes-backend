package com.herokuapp.erpmesbackend.erpmesbackend.communication.dto;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Message;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MessageDTO {

    private long id;
    private String content;
    private EmployeeDTO authorDTO;
    private Long channelId;
    private LocalDateTime creationTime;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.authorDTO = new EmployeeDTO(message.getAuthor());
        this.channelId = message.getChannelId();
        this.creationTime = message.getCreationTime();
    }

    public MessageDTO(String content, EmployeeDTO authorDTO, Long channelId) {
        this.content = content;
        this.authorDTO = authorDTO;
        this.channelId = channelId;
    }
}
