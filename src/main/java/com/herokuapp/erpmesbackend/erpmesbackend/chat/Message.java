package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
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

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    public Message(String content, Employee author, Channel channel) {
        this.content = content;
        this.author = author;
        this.creationTime = LocalDateTime.now();
        this.channel = channel;
    }

    public boolean checkIfDataEquals(Message message) {
        return content.equals(message.getContent()) &&
                author.checkIfDataEquals(message.getAuthor()) &&
                channel.checkIfDataEquals(message.getChannel());
    }
}
