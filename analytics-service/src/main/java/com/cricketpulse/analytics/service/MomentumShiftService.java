package com.cricketpulse.analytics.service;

import com.cricketpulse.common.model.BallEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MomentumShiftService {

    // matchId -> last 18 balls
    private final Map<String, List<BallEvent>> recentBallsMap = new ConcurrentHashMap<>();

    private static final int WINDOW_SIZE = 18;

    public MomentumResult detectMomentum(BallEvent ballEvent) {
        updateRecentBalls(ballEvent);

        List<BallEvent> recentBalls = recentBallsMap.get(ballEvent.getMatchId());

        if (recentBalls.size() < 3) {
            return new MomentumResult("NEUTRAL", 50.0, "Not enough data yet");
        }

        int totalRuns = recentBalls.stream()
                .mapToInt(BallEvent::getRunsScored)
                .sum();

        long wickets = recentBalls.stream()
                .filter(BallEvent::isWicket)
                .count();

        long boundaries = recentBalls.stream()
                .filter(b -> b.getRunsScored() >= 4)
                .count();

        long dotBalls = recentBalls.stream()
                .filter(b -> b.getRunsScored() == 0
                        && !b.isWide()
                        && !b.isNoBall())
                .count();

        return calculateMomentum(totalRuns, wickets, boundaries, dotBalls, recentBalls.size());
    }

    private void updateRecentBalls(BallEvent ballEvent) {
        recentBallsMap.putIfAbsent(ballEvent.getMatchId(), new ArrayList<>());
        List<BallEvent> balls = recentBallsMap.get(ballEvent.getMatchId());
        balls.add(ballEvent);
        if (balls.size() > WINDOW_SIZE) {
            balls.remove(0);
        }
    }

    private MomentumResult calculateMomentum(int totalRuns, long wickets,
                                             long boundaries, long dotBalls,
                                             int ballCount) {
        double battingScore = 0;
        double bowlingScore = 0;

        // Runs factor
        double runsPerBall = (double) totalRuns / ballCount;
        if (runsPerBall >= 2.0) battingScore += 40;
        else if (runsPerBall >= 1.5) battingScore += 25;
        else if (runsPerBall >= 1.0) battingScore += 10;

        // Boundaries factor
        if (boundaries >= 3) battingScore += 30;
        else if (boundaries >= 2) battingScore += 20;
        else if (boundaries >= 1) battingScore += 10;

        // Wickets factor
        if (wickets >= 2) bowlingScore += 50;
        else if (wickets == 1) bowlingScore += 25;

        // Dot balls factor
        if (dotBalls >= 4) bowlingScore += 30;
        else if (dotBalls >= 2) bowlingScore += 15;

        double totalScore = battingScore - bowlingScore;
        double momentumScore = 50 + (totalScore / 2);
        momentumScore = Math.max(0, Math.min(100, momentumScore));

        String team;
        String reason;

        if (momentumScore >= 70) {
            team = "BATTING";
            reason = String.format("Batting dominant — %d runs, %d boundaries in last %d balls",
                    totalRuns, boundaries, ballCount);
        } else if (momentumScore <= 30) {
            team = "BOWLING";
            reason = String.format("Bowling dominant — %d wickets, %d dot balls in last %d balls",
                    wickets, dotBalls, ballCount);
        } else {
            team = "NEUTRAL";
            reason = "Match evenly poised";
        }

        log.info("MOMENTUM SHIFT | {} momentum | Score: {} | {}",
                team, Math.round(momentumScore), reason);

        return new MomentumResult(team, momentumScore, reason);
    }

    public record MomentumResult(
            String momentum,
            double score,
            String reason
    ) {}
}