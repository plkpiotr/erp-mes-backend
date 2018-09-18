package com.herokuapp.erpmesbackend.erpmesbackend.shop.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.service.EmailService;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.service.ShopService;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ShopServiceRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.ReturnRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.ReturnStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class ReturnController {

    private final ReturnRepository returnRepository;
    private final ShopService shopService;
    private final EmailService emailService;

    @Autowired
    public ReturnController(ReturnRepository returnRepository, ShopService shopService,
                            EmailService emailService) {
        this.returnRepository = returnRepository;
        this.shopService = shopService;
        this.emailService = emailService;
    }

    @GetMapping("/returns")
    @ResponseStatus(HttpStatus.OK)
    public List<Return> getAllOrders() {
        return returnRepository.findAll();
    }

    @PostMapping("/returns")
    @ResponseStatus(HttpStatus.CREATED)
    public Return addOnReturn(@RequestBody ShopServiceRequest request) {
        Return r = shopService.addNewReturn(request);
        emailService.sendNewReturnRegisteredMessage(r.getId());
        return r;
    }

    @GetMapping("/returns/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Return getOneReturn(@PathVariable("id") Long id) {
        shopService.checkIfReturnExists(id);
        return returnRepository.findById(id).get();
    }

    @PutMapping("/returns/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Return updateStatusReturn(@PathVariable("id") Long id, @RequestBody String status) {
        shopService.checkIfReturnExists(id);
        emailService.sendReturnStatusChangeMessage(id, status);
        return shopService.updateReturnStatus(id, ReturnStatus.valueOf(status));
    }
}
