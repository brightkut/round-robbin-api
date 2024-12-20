package com.coder.routingapi.service;

import com.coder.routingapi.model.ServerInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthCheckServiceTest {
    @InjectMocks
    private HealthCheckService healthCheckService;

    @Mock
    private LoadBalanceService loadBalanceService;

    @Test
    public void checkAllInstanceHealthAllInstanceHealthy_success() {
        LocalDateTime now = LocalDateTime.now();

        List<ServerInstance> mockServerInstances = new ArrayList<>();

        ServerInstance mockServerInstance = new ServerInstance(
                "8081",
                "localhost",
                8081,
                true,
                now,
                0
        );

        mockServerInstances.add(mockServerInstance);

        when(loadBalanceService.getAllInstances()).thenReturn(mockServerInstances);

        healthCheckService.checkAllInstanceHealth();

        verify(loadBalanceService, times(1)).getAllInstances();
        verify(loadBalanceService, times(0)).removeInstance(anyString());
    }

    @Test
    public void checkAllInstanceHealthNoInstanceHealthy_success() {
        LocalDateTime now = LocalDateTime.now();

        List<ServerInstance> mockServerInstances = new ArrayList<>();

        ServerInstance mockServerInstance = new ServerInstance(
                "8081",
                "localhost",
                8081,
                false,
                now,
                0
        );

        mockServerInstances.add(mockServerInstance);

        when(loadBalanceService.getAllInstances()).thenReturn(mockServerInstances);

        healthCheckService.checkAllInstanceHealth();

        verify(loadBalanceService, times(1)).getAllInstances();
        verify(loadBalanceService, times(1)).removeInstance(anyString());
    }
}
