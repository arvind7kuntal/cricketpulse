package com.cricketpulse.analytics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MatchAnalytics {

    private String matchId;
    private int overNumber;
    private int ballNumber;
    private String batsmanName;
    private String bowlerName;

    // Pressure Index
    private double pressureIndex;

    // Over Prediction
    private double predictedRuns;
    private String runRange;
    private double wicketProbability;

    // Momentum
    private String momentum;
    private double momentumScore;
    private String momentumReason;

    // Timestamp
    private LocalDateTime calculatedAt;
}