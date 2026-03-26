package com.cricketpulse.analytics.service;

import com.cricketpulse.common.model.BallEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PressureIndexService {

    public double calculatePressureIndex(BallEvent ballEvent) {

        double rrpPressure = calculateRunRatePressure(ballEvent);
        double wicketPressure = calculateWicketPressure(ballEvent);
        double phasePressure = calculatePhasePressure(ballEvent);
        double dotBallPressure = calculateDotBallPressure(ballEvent);

        double totalPressure = rrpPressure + wicketPressure + phasePressure + dotBallPressure;

        log.info("Pressure Index Breakdown | Match: {} | Over: {}.{} | " +
                        "RRP: {} | Wickets: {} | Phase: {} | DotBall: {} | TOTAL: {}",
                ballEvent.getMatchId(),
                ballEvent.getOverNumber(),
                ballEvent.getBallNumber(),
                rrpPressure, wicketPressure, phasePressure, dotBallPressure,
                totalPressure);

        return totalPressure;
    }

    private double calculateRunRatePressure(BallEvent ballEvent) {
        int ballsBowled = (ballEvent.getOverNumber() * 6) + ballEvent.getBallNumber();
        int ballsRemaining = 120 - ballsBowled;

        if (ballsRemaining <= 0) return 40.0;

        int runsRequired = 180 - ballEvent.getTotalRunsSoFar();
        if (runsRequired <= 0) return 0.0;

        double requiredRunRate = (runsRequired * 6.0) / ballsRemaining;
        double currentRunRate = (ballEvent.getTotalRunsSoFar() * 6.0) / Math.max(ballsBowled, 1);

        double rrpDiff = requiredRunRate - currentRunRate;

        if (rrpDiff <= 0) return 5.0;
        else if (rrpDiff <= 2) return 15.0;
        else if (rrpDiff <= 4) return 25.0;
        else if (rrpDiff <= 6) return 33.0;
        else return 40.0;
    }

    private double calculateWicketPressure(BallEvent ballEvent) {
        int wickets = ballEvent.getTotalWicketsSoFar();
        if (wickets <= 1) return 5.0;
        else if (wickets <= 3) return 10.0;
        else if (wickets <= 5) return 17.0;
        else if (wickets <= 7) return 22.0;
        else return 25.0;
    }

    private double calculatePhasePressure(BallEvent ballEvent) {
        int over = ballEvent.getOverNumber();
        if (over <= 5) return 10.0;
        else if (over <= 15) return 15.0;
        else return 20.0;
    }

    private double calculateDotBallPressure(BallEvent ballEvent) {
        if (ballEvent.getRunsScored() == 0 && !ballEvent.isWide() && !ballEvent.isNoBall()) {
            return 15.0;
        }
        return 0.0;
    }
}
