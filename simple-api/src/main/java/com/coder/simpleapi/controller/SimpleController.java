package com.coder.simpleapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/simples")
@RestController
public class SimpleController {

    @PostMapping
    Map<String, Object> sendBackJson(@RequestBody Map<String, Object> req , @RequestParam(defaultValue ="0") String respTime) throws InterruptedException {

        //TODO Uncomment for set slow response time
        Thread.sleep(Long.parseLong(respTime) * 1000);
        return req;
    }
}
