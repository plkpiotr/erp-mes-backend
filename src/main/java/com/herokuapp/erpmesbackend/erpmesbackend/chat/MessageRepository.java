package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
