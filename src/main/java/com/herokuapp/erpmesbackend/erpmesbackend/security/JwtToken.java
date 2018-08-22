package com.herokuapp.erpmesbackend.erpmesbackend.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtToken {

    private String token;
}
