package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<List<Channel>> findByParticipantsId(Long id);

    Optional<List<Channel>> findByParticipantsEmail(String username);
}
