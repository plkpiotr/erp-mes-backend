package com.herokuapp.erpmesbackend.erpmesbackend.communication.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class EmailEntityRequest {

    private String email;

    @NotNull
    private String subject;

    @NotNull
    private List<String> content;

}
