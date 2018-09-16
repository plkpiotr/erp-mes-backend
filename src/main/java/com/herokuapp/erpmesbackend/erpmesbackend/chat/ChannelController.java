package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.ForbiddenException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ChannelController {

    private final ChannelRepository channelRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ChannelController(ChannelRepository channelRepository, EmployeeRepository employeeRepository) {
        this.channelRepository = channelRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/channels")
    @ResponseStatus(HttpStatus.OK)
    public List<ChannelDTO> getAllChannels() {
        List<Channel> channels = channelRepository.findAll();
        List<ChannelDTO> channelDTOs = new ArrayList<>();
        channels.forEach(channel -> channelDTOs.add(new ChannelDTO(channel)));
        return channelDTOs;
    }

    @GetMapping("/channels/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ChannelDTO getOneChannel(@PathVariable("id") Long id) {
        checkIfChannelExists(id);
        checkIfEmployeeHasAccess(id);
        return new ChannelDTO(channelRepository.findById(id).get());
    }

    @GetMapping("/employees/{id}/channels")
    @ResponseStatus(HttpStatus.OK)
    public List<ChannelDTO> getChannelsByParticipant(@PathVariable("id") Long id) {
        checkIfParticipantExists(id);

        if (!channelRepository.findByParticipantsId(id).isPresent())
            return new ArrayList<>();

        checkIfEmployeeHasAccess(id);
        List<Channel> channels = channelRepository.findByParticipantsId(id).get();
        List<ChannelDTO> channelDTOs = new ArrayList<>();
        channels.forEach(channel -> channelDTOs.add(new ChannelDTO(channel)));

        return channelDTOs;
    }

    @PostMapping("/channels")
    @ResponseStatus(HttpStatus.CREATED)
    public ChannelDTO addOneChannel(@RequestBody ChannelRequest channelRequest) {
        String name = channelRequest.getName();

        checkIfParticipantListIsEmpty(channelRequest.getParticipantIds());
        List<Employee> participants = new ArrayList<>();
        channelRequest.getParticipantIds().forEach(this::checkIfParticipantExists);
        channelRequest.getParticipantIds().forEach(id -> participants.add(employeeRepository.findById(id).get()));

        Channel channel = new Channel(name, participants);
        channelRepository.save(channel);
        return new ChannelDTO(channel);
    }

    @PutMapping("/channels/{id}")
    public HttpStatus updateChannel(@PathVariable("id") Long id, @RequestBody ChannelRequest channelRequest) {
        checkIfChannelExists(id);
        Channel channel = channelRepository.findById(id).get();

        channel.setName(channelRequest.getName());

        checkIfParticipantListIsEmpty(channelRequest.getParticipantIds());
        List<Employee> participants = new ArrayList<>();
        if (channelRequest.getParticipantIds() != null) {
            channelRequest.getParticipantIds().forEach(this::checkIfParticipantExists);
            channelRequest.getParticipantIds().forEach(index -> participants.add(employeeRepository.findById(index).get()));
        }
        channel.setParticipants(participants);

        channelRepository.save(channel);
        return HttpStatus.NO_CONTENT;
    }

    @DeleteMapping("/channels/{id}")
    public HttpStatus removeChannel(@PathVariable("id") Long id) {
        checkIfChannelExists(id);
        channelRepository.delete(channelRepository.findById(id).get());
        return HttpStatus.OK;
    }

    private void checkIfChannelExists(Long id) {
        if (!channelRepository.findById(id).isPresent())
            throw new NotFoundException("Such channel doesn't exist!");
    }

    private void checkIfParticipantExists(Long id) {
        if (!employeeRepository.findById(id).isPresent())
            throw new NotFoundException("At least one of the participant doesn't exist!");
    }

    private void checkIfParticipantListIsEmpty(List<Long> participantIds) {
        if (participantIds.isEmpty())
            throw new InvalidRequestException("List of participants can't be empty!");
    }

    private void checkIfEmployeeHasAccess(Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        if (!channelRepository.findByParticipantsEmail(username).isPresent())
            throw new ForbiddenException("You don't have access to this resource!");
    }
}
