package com.herokuapp.erpmesbackend.erpmesbackend.communication.request;

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
    private String name;

    @NonNull
    private String description;

    @NonNull
    private List<Long> recipientIds;
}
