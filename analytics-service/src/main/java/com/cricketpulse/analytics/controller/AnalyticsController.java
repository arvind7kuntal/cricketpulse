package com.cricketpulse.analytics.controller;

import com.cricketpulse.analytics.model.MatchAnalytics;
import com.cricketpulse.analytics.service.AnalyticsStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsStore analyticsStore;

    @GetMapping("/{matchId}")
    public ResponseEntity<MatchAnalytics> getMatchAnalytics(@PathVariable String matchId) {
        log.info("Analytics requested for match: {}", matchId);

        MatchAnalytics analytics = analyticsStore.getLatest(matchId);

        if (analytics == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/live")
    public ResponseEntity<Map<String, MatchAnalytics>> getAllLiveMatches() {
        log.info("All live matches analytics requested");
        return ResponseEntity.ok(analyticsStore.getAllMatches());
    }
}