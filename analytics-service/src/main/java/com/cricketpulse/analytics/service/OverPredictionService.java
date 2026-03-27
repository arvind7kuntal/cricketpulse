package com.cricketpulse.analytics.service;

import com.cricketpulse.common.model.BallEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OverPredictionService {

    public OverPrediction predictNextOver(BallEvent ballEvent) {

        double baseRuns = 8.0;

        double phaseBonus = calculatePhaseBonus(ballEvent);
        double chaseBonus = calculateChaseBonus(ballEvent);
        double scoringRateAdjustment = calculateScoringRateAdjustment(ballEvent);
        double wicketPenalty = calculateWicketPenalty(ballEvent);

        double predictedRuns = baseRuns + phaseBonus + chaseBonus
                + scoringRateAdjustment + wicketPenalty;

        predictedRuns = Math.max(4, Math.min(24, predictedRuns));

        double wicketProbability = calculateWicketProbability(ballEvent);

        String runRange = getRunRange(predictedRuns);

        log.info("Over Prediction | Match: {} | Over: {} | " +
                        "Predicted next over: {} runs ({}) | Wicket probability: {}%",
                ballEvent.getMatchId(),
                ballEvent.getOverNumber() + 1,
                Math.round(predictedRuns),
                runRange,
                Math.round(wicketProbability));

        return new OverPrediction(predictedRuns, runRange, wicketProbability);
    }

    private double calculatePhaseBonus(BallEvent ballEvent) {
        int over = ballEvent.getOverNumber();
        if (over < 5) return 2.0;        // powerplay — batting friendly
        else if (over >= 15) return 3.0; // death overs — batsmen attack
        else return 0.0;                 // middle overs — bowlers dominate
    }

    private double calculateChaseBonus(BallEvent ballEvent) {
        if (ballEvent.getInningNumber() == 2) {
            int ballsBowled = (ballEvent.getOverNumber() * 6) + ballEvent.getBallNumber();
            int ballsRemaining = 120 - ballsBowled;
            int runsRequired = 180 - ballEvent.getTotalRunsSoFar();

            if (ballsRemaining <= 0) return 0.0;

            double requiredRunRate = (runsRequired * 6.0) / ballsRemaining;

            if (requiredRunRate > 12) return 3.0;
            else if (requiredRunRate > 9) return 2.0;
            else if (requiredRunRate > 7) return 1.0;
            else return 0.0;
        }
        return 0.0;
    }

    private double calculateScoringRateAdjustment(BallEvent ballEvent) {
        int ballsBowled = (ballEvent.getOverNumber() * 6) + ballEvent.getBallNumber();
        if (ballsBowled == 0) return 0.0;

        double currentRunRate = (ballEvent.getTotalRunsSoFar() * 6.0) / ballsBowled;

        if (currentRunRate > 10) return 2.0;
        else if (currentRunRate > 8) return 1.0;
        else if (currentRunRate < 6) return -1.0;
        else return 0.0;
    }

    private double calculateWicketPenalty(BallEvent ballEvent) {
        int wickets = ballEvent.getTotalWicketsSoFar();
        if (wickets >= 8) return -2.0;
        else if (wickets >= 6) return -1.0;
        else return 0.0;
    }

    private double calculateWicketProbability(BallEvent ballEvent) {
        double base = 15.0;

        int over = ballEvent.getOverNumber();
        if (over >= 15) base += 5.0;
        if (ballEvent.getInningNumber() == 2) base += 3.0;
        if (ballEvent.getTotalWicketsSoFar() >= 6) base += 5.0;

        return Math.min(base, 60.0);
    }

    private String getRunRange(double predictedRuns) {
        int runs = (int) Math.round(predictedRuns);
        if (runs <= 6) return "4-6";
        else if (runs <= 9) return "7-9";
        else if (runs <= 12) return "10-12";
        else return "13+";
    }

    public record OverPrediction(
            double predictedRuns,
            String runRange,
            double wicketProbability
    ) {}
}