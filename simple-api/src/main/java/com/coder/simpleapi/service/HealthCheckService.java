package com.coder.simpleapi.service;

import com.coder.simpleapi.dto.RegisterInstanceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;


@Service
public class HealthCheckService {

    private static final Logger log = LoggerFactory.getLogger(HealthCheckService.class);

    @Value("${routing-api.host}")
    private String routingApiHost;

    @Value("${server.port}")
    private int port;


    private final RestTemplate restTemplate;

    public HealthCheckService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRateString = "${schedule.fixRate}", initialDelayString = "${schedule.initialDelay}")
    public void sendHealthCheck() throws UnknownHostException {
        RegisterInstanceDTO req = new RegisterInstanceDTO(
                Integer.toString(port),
                InetAddress.getLocalHost().getCanonicalHostName(),
                port
        );

        log.info("Send health check request to {} with request: {}", "routingApiHost", req);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<RegisterInstanceDTO> entity = new HttpEntity<>(req, headers);

            // Call load balance service
            ResponseEntity<String> res = restTemplate.postForEntity(
                    routingApiHost + "/load-balances/register",
                    entity,
                    String.class
            );

            // Log the response
//            log.info("Response from routing API endpoint /load-balances/register: {}", res.getBody());
        } catch (Exception e) {
            // Handle errors
            log.error("Error occurred while calling routing API endpoint /load-balances/register: {}", e.getMessage(), e);
        }
    }
}
