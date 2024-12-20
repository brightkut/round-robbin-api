package com.coder.routingapi.controller;

import com.coder.routingapi.dto.RegisterInstanceDTO;
import com.coder.routingapi.service.LoadBalanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/load-balances")
@RestController
public class LoadBalanceController {
    private final LoadBalanceService loadBalanceService;

    public LoadBalanceController(LoadBalanceService loadBalanceService) {
        this.loadBalanceService = loadBalanceService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerInstance(@RequestBody RegisterInstanceDTO req){
        loadBalanceService.registerInstance(req);

        return ResponseEntity.ok(String.format("Register instance with hostname: %s and port: %s successfully", req.hostName(), req.port()));
    }
}
