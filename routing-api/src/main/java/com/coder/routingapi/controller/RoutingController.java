package com.coder.routingapi.controller;

import com.coder.routingapi.service.RoutingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/routings")
@RestController
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @PostMapping
    Map<String, Object> sendReqJson(@RequestBody Map<String, Object> req, @RequestParam(defaultValue ="0") String respTime){
        return routingService.sendReqJson(req, respTime);
    }
}
