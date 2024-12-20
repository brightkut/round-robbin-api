package com.coder.routingapi.service;

import com.coder.routingapi.model.ServerInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.coder.routingapi.constant.Constant.HEALTH_CHECK_INTERVAL;

@Service
public class HealthCheckService {
    private static final Logger log = LoggerFactory.getLogger(HealthCheckService.class);

    private final LoadBalanceService loadBalancerService;

    public HealthCheckService(LoadBalanceService loadBalancerService) {
        this.loadBalancerService = loadBalancerService;
    }

    @Scheduled(fixedRateString = "${schedule.fixRate}", initialDelayString = "${schedule.initialDelay}")
    public void checkAllInstanceHealth(){
//        log.info("Trigger check all instance health");
        List<ServerInstance> serverInstances = loadBalancerService.getAllInstances();

        log.info("Found {} instances and start check health", serverInstances.size());

        if(!CollectionUtils.isEmpty(serverInstances)){
            LocalDateTime now = LocalDateTime.now();

            serverInstances.stream()
                    .filter(serverInstance -> !isInstanceHealthy(serverInstance, now))
                    .forEach(serverInstance -> loadBalancerService.removeInstance(serverInstance.getInstanceId()));
        }
    }

    private boolean isInstanceHealthy(ServerInstance serverInstance, LocalDateTime now) {
        LocalDateTime nextHealthCheckTime = serverInstance.getLastHealthCheckTime().plusSeconds(HEALTH_CHECK_INTERVAL);

        if(now.isAfter(nextHealthCheckTime) || !serverInstance.getIsHealthy()){
            log.warn("Instance id: {}, Host: {} is not healthy.", serverInstance.getInstanceId(), serverInstance.getHostName().concat(Integer.toString(serverInstance.getPort())));
            return false;
        }

        log.info("Instance id: {}, Host: {} is healthy.", serverInstance.getInstanceId(), serverInstance.getHostName().concat(Integer.toString(serverInstance.getPort())));
        return true;
    }
}
