package com.herokuapp.erpmesbackend.erpmesbackend.suggestions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionRequest {

    @NonNull
    private Phase phase;

    @NonNull
    private String name;

    @NonNull
    private String details;

    private Long authorId;

    @NonNull
    private List<Long> recipientIds;
}
