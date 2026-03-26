package com.cricketpulse.analytics.consumer;

import com.cricketpulse.analytics.service.PressureIndexService;
import com.cricketpulse.common.model.BallEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class BallEventConsumer {
    private final PressureIndexService pressureIndexService;

    @KafkaListener(
            topics="ball-events",
            groupId = "analytics-group"
    )

    public void consumeBallEvent(BallEvent ballEvent)
    {
        log.info("Received ball event from Kafka | Match: {} | Over: {}.{} | Runs: {} | Bowler: {} | Batsman: {}",
            ballEvent.getMatchId(),
            ballEvent.getOverNumber(),
            ballEvent.getBallNumber(),
            ballEvent.getRunsScored(),
            ballEvent.getBowlerName(),
            ballEvent.getBatsmanName());

        double pressureIndex= pressureIndexService.calculatePressureIndex(ballEvent);

        log.info("PRESSURE INDEX for {} at Over {}.{} → {}",
                ballEvent.getBatsmanName(),
                ballEvent.getOverNumber(),
                ballEvent.getBallNumber(),
                pressureIndex);
    }
}
