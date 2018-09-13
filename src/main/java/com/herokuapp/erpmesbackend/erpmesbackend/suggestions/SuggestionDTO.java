package com.herokuapp.erpmesbackend.erpmesbackend.suggestions;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeDTO;
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
    private EmployeeDTO authorDTO;
    private List<EmployeeDTO> recipientDTOs;
    private LocalDateTime creationTime;

    public SuggestionDTO(Suggestion suggestion) {
        this.id = suggestion.getId();
        this.phase = suggestion.getPhase();
        this.name = suggestion.getName();
        this.description = suggestion.getDescription();
        if (suggestion.getAuthor() != null)
            this.authorDTO = new EmployeeDTO(suggestion.getAuthor());
        this.recipientDTOs = new ArrayList<>();
        suggestion.getRecipients().forEach(recipient -> this.recipientDTOs.add(new EmployeeDTO(recipient)));
        this.creationTime = suggestion.getCreationTime();
    }

    public SuggestionDTO(String name, String description, EmployeeDTO authorDTO,
                         List<EmployeeDTO> recipientDTOs) {
        this.name = name;
        this.description = description;
        this.authorDTO = authorDTO;
        this.recipientDTOs = recipientDTOs;
    }

    public boolean checkIfDataEquals(SuggestionDTO suggestionDTO) {
        return phase.equals(suggestionDTO.getPhase()) &&
                name.equals(suggestionDTO.getName()) &&
                description.equals(suggestionDTO.getDescription()) &&
                // authorDTO.equals(suggestionDTO.getAuthorDTO()) &&
                compareRecipientDTOs(suggestionDTO.getRecipientDTOs()) &&
                creationTime.equals(suggestionDTO.getCreationTime());
    }

    private boolean compareRecipientDTOs(List<EmployeeDTO> recipientDTOList) {
        if (recipientDTOList.isEmpty())
            return true;
        for (EmployeeDTO recipientDTO : recipientDTOs) {
            if (recipientDTOList.stream().noneMatch(t -> t.checkIfDataEquals(recipientDTO)))
                return false;
        }
        return true;
    }
}
