package com.herokuapp.erpmesbackend.erpmesbackend.communication.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelRequest {

    @NonNull
    private String name;

    @NonNull
    private List<Long> participantIds;
}
