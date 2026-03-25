package com.cricketpulse.matchingestion.controller;

import com.cricketpulse.common.model.BallEvent;
import com.cricketpulse.matchingestion.producer.BallEventProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ball-events")
public class BallEventController {
    private final BallEventProducer ballEventProducer;


    @PostMapping
    public ResponseEntity<String> receiveBallEvent(@Valid @RequestBody BallEvent ballEvent) {
        log.info("Received ball event for match: {}", ballEvent.getMatchId());
        ballEventProducer.publishBallEvent(ballEvent);
        return ResponseEntity.ok("Ball event received and published successfully");
    }
}
