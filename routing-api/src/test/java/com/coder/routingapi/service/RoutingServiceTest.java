package com.coder.routingapi.service;

import com.coder.routingapi.model.ServerInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoutingServiceTest {

    @InjectMocks
    private RoutingService routingService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private LoadBalanceService loadBalanceService;

    @Test
    public void sendReqJson_success() {
        ServerInstance mockServerInstance = new ServerInstance(
                "8081",
                "localhost",
                8081,
                false,
                LocalDateTime.now(),
                0
        );

        Map<String, Object> req = new HashMap<>();
        req.put("game", "Mobile Legends");
        req.put("gamerID", "GYUTDTE");
        req.put("points", 20);

        Map<String, Object> mockResponse = new HashMap<>(req);
        ResponseEntity<Map<String, Object>> mockSimpleApiRes = ResponseEntity.ok(mockResponse);

        doReturn(mockServerInstance).when(loadBalanceService).getNextAvailableInstance();
        doReturn(mockSimpleApiRes)
                .when(restTemplate)
                .exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class));

        doNothing().when(loadBalanceService).updateResponseTime(anyString(), anyLong());

        Map<String, Object> actual = routingService.sendReqJson(req, null);

        assertEquals("Mobile Legends", actual.get("game"));
        assertEquals("GYUTDTE", actual.get("gamerID"));
        assertEquals(20, actual.get("points"));

        verify(loadBalanceService, times(1)).getNextAvailableInstance();
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
        verify(loadBalanceService, times(1)).updateResponseTime(eq("8081"), anyLong());
    }

}
