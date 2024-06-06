package com.jrsf.httpsgslbapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class TrafficController {

    @GetMapping(" ")
    public TrafficData getTraffic(){
        Random random = new Random();
        return new TrafficData(random.nextInt(100), random.nextInt(50));
    }

    static class TrafficData{
        private int activeConnections;
        private int requestsPerSecond;

        public TrafficData(int activeConnections, int requestsPerSecond){
            this.activeConnections = activeConnections;
            this.requestsPerSecond = requestsPerSecond;
        }

        public int getActiveConnections(){
            return activeConnections;
        }

        public int getRequestsPerSecond(){
            return requestsPerSecond;
        }
    }
}
