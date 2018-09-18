package com.herokuapp.erpmesbackend.erpmesbackend.communication.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Optional<List<Message>> findMessageByChannelIdOrderByCreationTimeDesc(Long id);
}
