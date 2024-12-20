package com.coder.routingapi.service;

import com.coder.routingapi.dto.RegisterInstanceDTO;
import com.coder.routingapi.model.ServerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.coder.routingapi.constant.Constant.MAX_RES_TIME_THRESHOLD;
import static com.coder.routingapi.constant.Constant.SLOW_INSTANCE_THRESHOLD_TIME;


@Service
public class LoadBalanceService {
    private static final Logger log = LoggerFactory.getLogger(LoadBalanceService.class);

    // key -> represent instance id (port), value -> sever instance information
    private final ConcurrentHashMap<String , ServerInstance> serverInstanceMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String , LocalDateTime> slowServerInstanceMap = new ConcurrentHashMap<>();

    private final AtomicInteger currentNodeIndex = new AtomicInteger(0);

    public LoadBalanceService() {
    }

    public void registerInstance(RegisterInstanceDTO req) {

        LocalDateTime now = LocalDateTime.now();

        ServerInstance serverInstance = new ServerInstance(
                req.instanceId(),
                req.hostName(),
                req.port(),
                true,
                now,
                0
        );

        if (!serverInstanceMap.containsKey(req.instanceId())) {
            log.info("Register instance: {} with this information: {} at time: {}", req.instanceId(), serverInstance, now);
        } else {
            serverInstance = serverInstanceMap.get(req.instanceId());
            LocalDateTime lastHealthCheckTime = serverInstance.getLastHealthCheckTime();
            serverInstance.setLastHealthCheckTime(now);

            log.info("Resend heartbeat instance: {} , lastHealthCheckTime: {} , new lastHealthCheckTime: {}", req.instanceId(), lastHealthCheckTime, now);
        }
        serverInstanceMap.put(req.instanceId(), serverInstance);
    }

    public List<ServerInstance> getAllInstances() {
        return new ArrayList<>(serverInstanceMap.values());
    }

    public ServerInstance getNextAvailableInstance() {
        recoverySlowInstances();

        List<ServerInstance> healthyInstances = serverInstanceMap.values().stream()
                    .filter(serverInstance -> serverInstance.getIsHealthy() && !slowServerInstanceMap.containsKey(serverInstance.getInstanceId()))
                    .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(healthyInstances)){
            throw new RuntimeException("No healthy instance available");
        }

        return this.getHealthyInstanceRoundRobin(healthyInstances);
    }

    public void updateResponseTime(String instanceId, long responseTime) {
        log.info("Update responseTime instance: {} with response time: {}", instanceId, responseTime);

        serverInstanceMap.computeIfPresent(instanceId, (id, serverInstance) -> {
            serverInstance.setResponseTime(responseTime);
            return serverInstance;  // Return the updated instance
        });

        // Track slow server instances
        trackSlowServerInstance(instanceId, responseTime);
    }


    public void removeInstance(String instanceId) {
        log.warn("Remove instance: {} at time: {} because it is not healthy", instanceId, LocalDateTime.now());
        serverInstanceMap.remove(instanceId);
        slowServerInstanceMap.remove(instanceId);
    }

    private ServerInstance getHealthyInstanceRoundRobin(List<ServerInstance> healthyInstances) {
        int index = currentNodeIndex.getAndIncrement() % healthyInstances.size();

        return healthyInstances.get(index);
    }

    private void trackSlowServerInstance(String instanceId, long responseTime) {
        if (responseTime >=MAX_RES_TIME_THRESHOLD) {
            log.info("Instance: {} has slow response time", instanceId);

            // use putIfAbsent prevent race condition
            slowServerInstanceMap.putIfAbsent(instanceId, LocalDateTime.now());
        }
    }

    private void recoverySlowInstances() {
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, LocalDateTime> entry : slowServerInstanceMap.entrySet()) {
            boolean isPastThreshold = now.isAfter(entry.getValue().plusSeconds(SLOW_INSTANCE_THRESHOLD_TIME));
            if (isPastThreshold) {
                String instanceId = entry.getKey();
                log.info("Recovery slow instance: {} , startSlowTime: {}, current time: {}", instanceId, entry.getValue(), now);
                // Check if the instanceId exists in serverInstanceMap and reset response time
                if (serverInstanceMap.containsKey(instanceId)) {
                    serverInstanceMap.get(instanceId).setResponseTime(0);
                }
                // Remove instance from slowServerInstanceMap as it has passed the threshold
                slowServerInstanceMap.remove(instanceId);
            }
        }
    }

}
