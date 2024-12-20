package com.coder.routingapi.model;

import java.time.LocalDateTime;


public class ServerInstance {
    private String instanceId;
    private String hostName;
    private int port;
    private Boolean isHealthy;
    private LocalDateTime lastHealthCheckTime;
    private long responseTime; // unit is seconds

    public ServerInstance() {
    }

    public ServerInstance(String instanceId, String hostName, int port, Boolean isHealthy, LocalDateTime lastHealthCheckTime, long responseTime) {
        this.instanceId = instanceId;
        this.hostName = hostName;
        this.port = port;
        this.isHealthy = isHealthy;
        this.lastHealthCheckTime = lastHealthCheckTime;
        this.responseTime = responseTime;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Boolean getIsHealthy() {
        return isHealthy;
    }

    public void setIsHealthy(Boolean isHealthy) {
        this.isHealthy = isHealthy;
    }

    public LocalDateTime getLastHealthCheckTime() {
        return lastHealthCheckTime;
    }

    public void setLastHealthCheckTime(LocalDateTime lastHealthCheckTime) {
        this.lastHealthCheckTime = lastHealthCheckTime;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    @Override
    public String toString() {
        return "ServerInstance{" +
                "instanceId='" + instanceId + '\'' +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", isHealthy=" + isHealthy +
                ", lastHealthCheckTime=" + lastHealthCheckTime +
                ", responseTime=" + responseTime +
                '}';
    }
}

