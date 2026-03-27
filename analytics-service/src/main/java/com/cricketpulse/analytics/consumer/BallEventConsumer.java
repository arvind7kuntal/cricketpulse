package com.cricketpulse.analytics.consumer;

import com.cricketpulse.analytics.model.MatchAnalytics;
import com.cricketpulse.analytics.service.AnalyticsStore;
import com.cricketpulse.analytics.service.MomentumShiftService;
import com.cricketpulse.analytics.service.OverPredictionService;
import com.cricketpulse.analytics.service.PressureIndexService;
import com.cricketpulse.common.model.BallEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BallEventConsumer {

    private final PressureIndexService pressureIndexService;
    private final OverPredictionService overPredictionService;
    private final MomentumShiftService momentumShiftService;
    private final AnalyticsStore analyticsStore;

    @KafkaListener(
            topics = "ball-events",
            groupId = "analytics-group"
    )
    public void consumeBallEvent(BallEvent ballEvent) {
        log.info("Received ball event from Kafka | Match: {} | Over: {}.{} | Runs: {} | Bowler: {} | Batsman: {}",
                ballEvent.getMatchId(),
                ballEvent.getOverNumber(),
                ballEvent.getBallNumber(),
                ballEvent.getRunsScored(),
                ballEvent.getBowlerName(),
                ballEvent.getBatsmanName());

        double pressureIndex = pressureIndexService.calculatePressureIndex(ballEvent);

        OverPredictionService.OverPrediction prediction =
                overPredictionService.predictNextOver(ballEvent);

        MomentumShiftService.MomentumResult momentum =
                momentumShiftService.detectMomentum(ballEvent);

        MatchAnalytics analytics = MatchAnalytics.builder()
                .matchId(ballEvent.getMatchId())
                .overNumber(ballEvent.getOverNumber())
                .ballNumber(ballEvent.getBallNumber())
                .batsmanName(ballEvent.getBatsmanName())
                .bowlerName(ballEvent.getBowlerName())
                .pressureIndex(pressureIndex)
                .predictedRuns(prediction.predictedRuns())
                .runRange(prediction.runRange())
                .wicketProbability(prediction.wicketProbability())
                .momentum(momentum.momentum())
                .momentumScore(momentum.score())
                .momentumReason(momentum.reason())
                .calculatedAt(LocalDateTime.now())
                .build();

        analyticsStore.save(analytics);

        log.info("Analytics saved | Match: {} | Pressure: {} | Next over: {} | Momentum: {}",
                ballEvent.getMatchId(),
                pressureIndex,
                prediction.runRange(),
                momentum.momentum());
    }
}