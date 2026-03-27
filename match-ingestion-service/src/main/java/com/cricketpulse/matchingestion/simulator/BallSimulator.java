package com.cricketpulse.matchingestion.simulator;

import com.cricketpulse.common.model.BallEvent;
import com.cricketpulse.matchingestion.producer.BallEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class BallSimulator {

    private final BallEventProducer ballEventProducer;
    private final Random random = new Random();

    private static final String MATCH_ID = "ipl_2025_rcb_mi_001";
    private static final List<String> BATSMEN = List.of(
            "Virat Kohli", "Rohit Sharma", "MS Dhoni",
            "Hardik Pandya", "Suryakumar Yadav", "Faf du Plessis"
    );
    private static final List<String> BOWLERS = List.of(
            "Jasprit Bumrah", "Mohammed Siraj", "Yuzvendra Chahal",
            "Hardik Pandya", "Deepak Chahar"
    );

    private int currentOver = 0;
    private int currentBall = 1;
    private int totalRuns = 0;
    private int totalWickets = 0;
    private boolean simulatorActive = true;

    @Scheduled(fixedDelay = 5000)
    public void simulateBall() {
        if (!simulatorActive) return;
        if (currentOver >= 20) {
            log.info("Match simulation complete — 20 overs done");
            simulatorActive = false;
            return;
        }

        int runsScored = generateRuns();
        boolean isWicket = generateWicket();
        boolean isNoBall = random.nextInt(20) == 0;
        boolean isWide = random.nextInt(15) == 0;

        if (!isWide && !isNoBall) {
            totalRuns += runsScored;
            if (isWicket) totalWickets++;
        }

        BallEvent ballEvent = BallEvent.builder()
                .matchId(MATCH_ID)
                .inningNumber(1)
                .overNumber(currentOver)
                .ballNumber(currentBall)
                .batsmanName(BATSMEN.get(random.nextInt(BATSMEN.size())))
                .bowlerName(BOWLERS.get(random.nextInt(BOWLERS.size())))
                .runsScored(runsScored)
                .isWicket(isWicket && !isNoBall)
                .isNoBall(isNoBall)
                .isWide(isWide)
                .totalRunsSoFar(totalRuns)
                .totalWicketsSoFar(totalWickets)
                .deliveryType(isNoBall ? "NO_BALL" : isWide ? "WIDE" : "NORMAL")
                .timestamp(LocalDateTime.now())
                .build();

        ballEventProducer.publishBallEvent(ballEvent);

        log.info("Simulated: Over {}.{} | {} runs | Wicket: {} | Total: {}/{}",
                currentOver, currentBall, runsScored,
                isWicket, totalRuns, totalWickets);

        advanceBall(isWide, isNoBall);
    }

    private int generateRuns() {
        int rand = random.nextInt(10);
        if (rand < 3) return 0;
        else if (rand < 5) return 1;
        else if (rand < 7) return 2;
        else if (rand == 7) return 4;
        else if (rand == 8) return 6;
        else return 3;
    }

    private boolean generateWicket() {
        return random.nextInt(12) == 0;
    }

    private void advanceBall(boolean isWide, boolean isNoBall) {
        if (!isWide && !isNoBall) {
            currentBall++;
            if (currentBall > 6) {
                currentBall = 1;
                currentOver++;
            }
        }
    }
}