package com.coder.simpleapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/simples")
@RestController
public class SimpleController {

    @PostMapping
    Map<String, Object> sendBackJson(@RequestBody Map<String, Object> req) throws InterruptedException {

        //TODO Uncomment for set slow response time
//        Thread.sleep(6000);
        return req;
    }
}
