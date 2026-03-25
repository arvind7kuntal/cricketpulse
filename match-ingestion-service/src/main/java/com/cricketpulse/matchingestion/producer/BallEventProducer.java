package com.cricketpulse.matchingestion.producer;

import com.cricketpulse.common.model.BallEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BallEventProducer {

    private static final String TOPIC="ball-events";
    private final KafkaTemplate<String, BallEvent> kafkaTemplate;

    public void publishBallEvent(BallEvent ballEvent)
    {
        log.info("Publishing ball event to topic: {} | Match: {} | Over: {}.{} | Runs: {}",TOPIC,ballEvent.getMatchId(),ballEvent.getOverNumber(),ballEvent.getBallNumber(),ballEvent.getRunsScored());

        kafkaTemplate.send(TOPIC,ballEvent.getMatchId(),ballEvent);
        log.info("Ball event published successfully for match: {}", ballEvent.getMatchId());
    }
}
