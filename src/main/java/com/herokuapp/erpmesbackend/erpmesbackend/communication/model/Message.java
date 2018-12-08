package com.herokuapp.erpmesbackend.erpmesbackend.communication.model;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String content;

    @OneToOne
    private Employee author;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    @Column(nullable = false)
    private Long channelId;

    public Message(String content, Employee author, Long channelId) {
        this.content = content;
        this.author = author;
        this.creationTime = LocalDateTime.now();
        this.channelId = channelId;
    }
}
