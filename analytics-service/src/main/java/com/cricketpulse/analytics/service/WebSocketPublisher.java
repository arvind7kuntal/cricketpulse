package com.cricketpulse.analytics.service;

import com.cricketpulse.analytics.model.MatchAnalytics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishMatchUpdate(MatchAnalytics analytics) {
        String topic = "/topic/match/" + analytics.getMatchId();

        messagingTemplate.convertAndSend(topic, analytics);

        log.info("WebSocket push → {} | Pressure: {} | Momentum: {}",
                topic,
                analytics.getPressureIndex(),
                analytics.getMomentum());
    }
}