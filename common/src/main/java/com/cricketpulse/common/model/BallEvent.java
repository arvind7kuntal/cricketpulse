package com.cricketpulse.common.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BallEvent {

    @NotBlank(message = "matchId is required")
    private String matchId;

    @Min(value = 1, message = "inning must be 1 or 2")
    @Max(value = 2, message = "inning must be 1 or 2")
    private int inningNumber;

    @Min(value = 0, message = "over cannot be negative")
    @Max(value = 19, message = "IPL has max 20 overs")
    private int overNumber;

    @Min(value = 1, message = "ball number starts from 1")
    private int ballNumber;

    @NotBlank(message = "batsmanName is required")
    private String batsmanName;

    @NotBlank(message = "bowlerName is required")
    private String bowlerName;

    @Min(value = 0, message = "runs cannot be negative")
    @Max(value = 10, message = "runs per delivery cannot exceed 10")
    private int runsScored;

    private boolean isWicket;
    private boolean isNoBall;
    private boolean isWide;

    @Min(value = 0, message = "total runs cannot be negative")
    private int totalRunsSoFar;

    @Min(value = 0, message = "wickets cannot be negative")
    @Max(value = 10, message = "wickets cannot exceed 10")
    private int totalWicketsSoFar;

    @NotBlank(message = "deliveryType is required")
    private String deliveryType;

    @NotNull(message = "timestamp is required")
    private LocalDateTime timestamp;
}
