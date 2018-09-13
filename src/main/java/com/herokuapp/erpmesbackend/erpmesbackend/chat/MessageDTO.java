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
    private LocalDateTime creationTime;
    private ChannelDTO channelDTO;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.authorDTO = new EmployeeDTO(message.getAuthor());
        this.channelDTO = new ChannelDTO(message.getChannel());
    }

    public MessageDTO(String content, EmployeeDTO authorDTO, ChannelDTO channelDTO) {
        this.content = content;
        this.authorDTO = authorDTO;
        this.channelDTO = channelDTO;
    }

    public boolean checkIfDataEquals(MessageDTO messageDTO) {
        return content.equals(messageDTO.getContent()) &&
                authorDTO.checkIfDataEquals(messageDTO.getAuthorDTO()) &&
                creationTime.equals(messageDTO.getCreationTime()) &&
                channelDTO.checkIfDataEquals(messageDTO.getChannelDTO());
    }
}
