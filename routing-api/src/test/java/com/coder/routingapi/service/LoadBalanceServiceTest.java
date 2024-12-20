package com.coder.routingapi.service;

import com.coder.routingapi.dto.RegisterInstanceDTO;
import com.coder.routingapi.model.ServerInstance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class LoadBalanceServiceTest {

    @InjectMocks
    private LoadBalanceService loadBalanceService;

    @Test
    public void registerInstanceNewInstance_success() {
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);

        loadBalanceService.registerInstance(req);

        List<ServerInstance> result = loadBalanceService.getAllInstances();

        assertEquals(1, result.size());
        assertEquals("8081", result.getFirst().getInstanceId());
        assertEquals("localhost", result.getFirst().getHostName());
        assertEquals(8081, result.getFirst().getPort());
        assertEquals(true, result.getFirst().getIsHealthy());
        assertEquals(0, result.getFirst().getResponseTime());
    }

    @Test
    public void registerInstanceUpdateLastHealthCheckTime_success(){
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);

        loadBalanceService.registerInstance(req);

        List<ServerInstance> result = loadBalanceService.getAllInstances();

        LocalDateTime lastHealthCheckTime = result.getFirst().getLastHealthCheckTime();

        loadBalanceService.registerInstance(req);

        List<ServerInstance> resultAfterUpdated = loadBalanceService.getAllInstances();

        assertEquals(1, result.size());
        assertEquals("8081", result.getFirst().getInstanceId());
        assertEquals("localhost", result.getFirst().getHostName());
        assertEquals(8081, result.getFirst().getPort());
        assertEquals(true, result.getFirst().getIsHealthy());
        assertEquals(0, result.getFirst().getResponseTime());
        assertNotEquals(0, lastHealthCheckTime.compareTo(resultAfterUpdated.getFirst().getLastHealthCheckTime()));
    }

    @Test
    public void getAllInstances_success() {
        List<ServerInstance> actual = loadBalanceService.getAllInstances();

        assertEquals(0, actual.size());
    }

    @Test
    public void getNextAvailableInstanceAllInstanceHealthy_success(){
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);
        loadBalanceService.registerInstance(req);
        RegisterInstanceDTO req2 = new RegisterInstanceDTO("8082", "localhost", 8082);
        loadBalanceService.registerInstance(req2);
        RegisterInstanceDTO req3 = new RegisterInstanceDTO("8083", "localhost", 8083);
        loadBalanceService.registerInstance(req3);

        ServerInstance actual = loadBalanceService.getNextAvailableInstance();

        assertEquals("8082", actual.getInstanceId());
        assertEquals("localhost", actual.getHostName());
        assertEquals(8082, actual.getPort());
        assertEquals(true, actual.getIsHealthy());
        assertEquals(0, actual.getResponseTime());
    }

    @Test
    public void getNextAvailableInstanceNoInstanceHealthy_failed() {
        assertThrows(RuntimeException.class, () -> {
            loadBalanceService.getNextAvailableInstance();
        });
    }

    @ParameterizedTest
    //@ValueSource(booleans = {true}) // use this when want to test recover
    @ValueSource(booleans = {false})
    public void getNextAvailableInstanceAllInstanceHealthyAndRecoverySlowInstance_success(boolean isRecovery) throws InterruptedException {
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);
        loadBalanceService.registerInstance(req);
        RegisterInstanceDTO req2 = new RegisterInstanceDTO("8082", "localhost", 8082);
        loadBalanceService.registerInstance(req2);
        RegisterInstanceDTO req3 = new RegisterInstanceDTO("8083", "localhost", 8083);
        loadBalanceService.registerInstance(req3);
        loadBalanceService.updateResponseTime(req3.instanceId(), 10);

        if(isRecovery) {
            Thread.sleep(10000);
        }

        ServerInstance actual = loadBalanceService.getNextAvailableInstance();

        assertEquals("8082", actual.getInstanceId());
        assertEquals("localhost", actual.getHostName());
        assertEquals(8082, actual.getPort());
        assertEquals(true, actual.getIsHealthy());
        assertEquals(0, actual.getResponseTime());
    }

    @Test
    public void updateResponseTime_success(){
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);
        loadBalanceService.registerInstance(req);

        loadBalanceService.updateResponseTime(req.instanceId(), 1);

        List<ServerInstance> actual = loadBalanceService.getAllInstances();

        assertEquals(1, actual.size());
        assertEquals("8081", actual.getFirst().getInstanceId());
        assertEquals("localhost", actual.getFirst().getHostName());
        assertEquals(8081, actual.getFirst().getPort());
        assertEquals(true, actual.getFirst().getIsHealthy());
        assertEquals(1, actual.getFirst().getResponseTime());
    }

    @Test
    public void updateResponseTimeSlowResponseTime_success(){
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);
        loadBalanceService.registerInstance(req);

        loadBalanceService.updateResponseTime(req.instanceId(), 10);

        List<ServerInstance> actual = loadBalanceService.getAllInstances();

        assertEquals(1, actual.size());
        assertEquals("8081", actual.getFirst().getInstanceId());
        assertEquals("localhost", actual.getFirst().getHostName());
        assertEquals(8081, actual.getFirst().getPort());
        assertEquals(true, actual.getFirst().getIsHealthy());
        assertEquals(10, actual.getFirst().getResponseTime());
    }

    @Test
    public void removeInstance_success(){
        RegisterInstanceDTO req = new RegisterInstanceDTO("8081", "localhost", 8081);
        loadBalanceService.registerInstance(req);
        RegisterInstanceDTO req2 = new RegisterInstanceDTO("8082", "localhost", 8082);
        loadBalanceService.registerInstance(req2);
        RegisterInstanceDTO req3 = new RegisterInstanceDTO("8083", "localhost", 8083);
        loadBalanceService.registerInstance(req3);

        List<ServerInstance> result = loadBalanceService.getAllInstances();

        int firstSize = result.size();

        loadBalanceService.removeInstance("8082");

        List<ServerInstance> actual = loadBalanceService.getAllInstances();

        assertEquals(2, actual.size());
        assertNotEquals(firstSize, actual.size());
    }
}
