package com.herokuapp.erpmesbackend.erpmesbackend.communication.dto;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Channel;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ChannelDTO {

    private long id;
    private String name;
    private List<EmployeeDTO> participantDTOs;

    public ChannelDTO(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.participantDTOs = new ArrayList<>();
        channel.getParticipants().forEach(participant -> this.participantDTOs.add(new EmployeeDTO(participant)));
    }

    public ChannelDTO(String name, List<EmployeeDTO> participantDTOs) {
        this.name = name;
        this.participantDTOs = participantDTOs;
    }
}
