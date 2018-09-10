package com.herokuapp.erpmesbackend.erpmesbackend.email;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;

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
    private String content;

    public EmailEntity(@Email String email, String subject, String content) {
        this.email = email;
        this.subject = subject;
        this.content = content;
    }
}
