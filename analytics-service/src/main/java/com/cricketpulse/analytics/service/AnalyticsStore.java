package com.cricketpulse.analytics.service;

import com.cricketpulse.analytics.model.MatchAnalytics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AnalyticsStore {

    private final Map<String, MatchAnalytics> latestAnalytics = new ConcurrentHashMap<>();

    public void save(MatchAnalytics analytics) {
        latestAnalytics.put(analytics.getMatchId(), analytics);
        log.info("Stored analytics for match: {}", analytics.getMatchId());
    }

    public MatchAnalytics getLatest(String matchId) {
        return latestAnalytics.get(matchId);
    }

    public Map<String, MatchAnalytics> getAllMatches() {
        return latestAnalytics;
    }
}