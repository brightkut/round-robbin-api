package com.coder.simpleapi.service;

import com.coder.simpleapi.dto.RegisterInstanceDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HealthCheckServiceTest {

    @Autowired
    private HealthCheckService healthCheckService;

    @MockitoBean
    private RestTemplate restTemplate;

    @Captor
    private ArgumentCaptor<String> urlHostCaptor;

    @Captor
    private ArgumentCaptor<HttpEntity<RegisterInstanceDTO>> httpEntityArgumentCaptor;

    @Test
    public void sendHealthCheck_success() throws UnknownHostException {

        ResponseEntity<String> expectedRes = ResponseEntity.ok("sendHealthCheck_success");

        when(restTemplate.postForEntity(urlHostCaptor.capture(), httpEntityArgumentCaptor.capture(), eq(String.class))).thenReturn(expectedRes);

        healthCheckService.sendHealthCheck();

        assertEquals("http://localhost:8080/load-balances/register", urlHostCaptor.getValue());
        assertNotNull(httpEntityArgumentCaptor.getValue());
        assertEquals("localhost", Objects.requireNonNull(httpEntityArgumentCaptor.getValue().getBody()).hostName());
        assertEquals(8081, Objects.requireNonNull(httpEntityArgumentCaptor.getValue().getBody()).port());

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());
    }
}
