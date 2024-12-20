package com.coder.routingapi.service;

import com.coder.routingapi.model.ServerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class RoutingService {
    private static final Logger log = LoggerFactory.getLogger(RoutingService.class);

    private final RestTemplate restTemplate;
    private final LoadBalanceService loadBalanceService;

    public RoutingService(RestTemplate restTemplate, LoadBalanceService loadBalanceService) {
        this.restTemplate = restTemplate;
        this.loadBalanceService = loadBalanceService;
    }

    public Map<String, Object> sendReqJson(Map<String, Object> req, String respTime) {

        ServerInstance nextAvailableInstance = loadBalanceService.getNextAvailableInstance();

        ResponseEntity<Map<String, Object>> res = null;

        try {
            long startTime = System.currentTimeMillis();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(req, headers);

            String simpleApiHost = String.format("http://%s:%d",
                    nextAvailableInstance.getHostName(),
                    nextAvailableInstance.getPort()
            );

            String url = UriComponentsBuilder.fromHttpUrl(simpleApiHost + "/simples")
                    .queryParam("respTime", respTime)
                    .toUriString();

            log.info("Sending request to simple api host: {} with payload: {}" ,simpleApiHost, req);

            // Call simple api service
             res = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );


            long endTime = System.currentTimeMillis();

            // Calculate response time
            long responseTime = (endTime - startTime) / 1000;

//            log.info("Response time when call simple api: {} seconds for instance id : {}", responseTime, nextAvailableInstance.getInstanceId());

            // Log the response
//            if(res.getBody() != null)  log.info("Response from simple API endpoint /simples: {}", res.getBody());

            // Update response time from calling api
            loadBalanceService.updateResponseTime(nextAvailableInstance.getInstanceId(), responseTime);

        } catch (Exception e) {
            // Handle errors
            log.error("Error occurred while calling simple API endpoint /simples: {}", e.getMessage(), e);
        }
        
        return res == null ? null : res.getBody();
    }
}
