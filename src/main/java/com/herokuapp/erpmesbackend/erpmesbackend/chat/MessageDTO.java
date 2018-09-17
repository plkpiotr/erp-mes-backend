package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeDTO;
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

    public boolean checkIfDataEquals(MessageDTO messageDTO) {
        return content.equals(messageDTO.getContent()) &&
                authorDTO.checkIfDataEquals(messageDTO.getAuthorDTO()) &&
                channelId.equals(messageDTO.getChannelId());
    }
}
