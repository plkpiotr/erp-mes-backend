package com.herokuapp.erpmesbackend.erpmesbackend.production.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Indicators;
import com.herokuapp.erpmesbackend.erpmesbackend.production.service.IndicatorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
public class IndicatorsController {

    private final IndicatorsService indicatorsService;

    @Autowired
    public IndicatorsController(IndicatorsService indicatorsService) {
        this.indicatorsService = indicatorsService;
    }

    @GetMapping("/indicators/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Indicators getIndicators(@PathVariable("id") Long id) {
        return indicatorsService.getIndicators(id);
    }
}
