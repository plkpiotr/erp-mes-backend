package com.herokuapp.erpmesbackend.erpmesbackend.communication.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Email
    private String email;
    private String subject;

    @ElementCollection
    private List<String> content;

    @Enumerated(EnumType.STRING)
    private EmailType emailType;

    LocalDateTime timestamp;

    public EmailEntity(@Email String email, String subject, List<String> content, EmailType emailType,
                       LocalDateTime timestamp) {
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.emailType = emailType;
        this.timestamp = timestamp;
    }
}
