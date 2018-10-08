package com.herokuapp.erpmesbackend.erpmesbackend.communication.dto;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Phase;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Suggestion;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class SuggestionDTO {

    private long id;
    private Phase phase;
    private String name;
    private String description;
    private EmployeeDTO author;
    private List<EmployeeDTO> recipients;
    private LocalDateTime creationTime;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EmployeeDTO startEmployee;
    private EmployeeDTO endEmployee;

    public SuggestionDTO(Suggestion suggestion) {
        this.id = suggestion.getId();
        this.phase = suggestion.getPhase();
        this.name = suggestion.getName();
        this.description = suggestion.getDescription();
        this.author = new EmployeeDTO(suggestion.getAuthor());
        this.recipients = new ArrayList<>();
        suggestion.getRecipients().forEach(recipient -> this.recipients.add(new EmployeeDTO(recipient)));
        this.creationTime = suggestion.getCreationTime();

        if (suggestion.getStartTime() != null) {
            this.startTime = suggestion.getStartTime();
            this.startEmployee = new EmployeeDTO(suggestion.getStartEmployee());
        }

        if (suggestion.getEndTime() != null) {
            this.endTime = suggestion.getEndTime();
            this.endEmployee = new EmployeeDTO(suggestion.getEndEmployee());
        }
    }

    public SuggestionDTO(String name, String description, EmployeeDTO author, List<EmployeeDTO> recipientDTOs) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.recipients = recipientDTOs;
    }

    public boolean checkIfDataEquals(SuggestionDTO suggestionDTO) {
        return name.equals(suggestionDTO.getName()) &&
                description.equals(suggestionDTO.getDescription()) &&
                // author.equals(suggestionDTO.getAuthor()) &&
                compareRecipientDTOs(suggestionDTO.getRecipients());
    }

    private boolean compareRecipientDTOs(List<EmployeeDTO> recipientDTOList) {
        for (EmployeeDTO recipientDTO : recipients) {
            if (recipientDTOList.stream().noneMatch(t -> t.checkIfDataEquals(recipientDTO))) {
                return false;
            }
        }
        return true;
    }
}
