import { useEffect, useState } from 'react';
import './App.css';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const MATCH_ID = 'ipl_2025_rcb_mi_001';

function App() {
  const [analytics, setAnalytics] = useState(null);
  const [connected, setConnected] = useState(false);
  const [ballHistory, setBallHistory] = useState([]);

useEffect(() => {
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8081/ws'),
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/match/${MATCH_ID}`, (message) => {
          const data = JSON.parse(message.body);
          setAnalytics(data);
          setBallHistory(prev => [data, ...prev].slice(0, 10));
        });
      },
      debug: () => {}
    });

    client.activate();

    return () => client.deactivate();
  }, []);

  if (!analytics) {
    return (
      <div className="loading">
        <h2>CricketPulse</h2>
        <p>{connected ? 'Waiting for match data...' : 'Connecting...'}</p>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="header">
        <h1>CricketPulse</h1>
        <span className={`status ${connected ? 'live' : 'offline'}`}>
          {connected ? 'LIVE' : 'OFFLINE'}
        </span>
      </header>

      <div className="match-info">
        <p className="match-title">RCB vs MI — IPL 2025</p>
        <p className="over-info">Over {analytics.overNumber}.{analytics.ballNumber}</p>
        <p className="players">{analytics.batsmanName} vs {analytics.bowlerName}</p>
      </div>

      <div className="metrics-grid">
        <div className="metric-card pressure">
          <div className="metric-label">Pressure Index</div>
          <div className="metric-value">{Math.round(analytics.pressureIndex)}</div>
          <div className="metric-sub">/ 100</div>
        </div>

        <div className="metric-card prediction">
          <div className="metric-label">Next Over</div>
          <div className="metric-value">{analytics.runRange}</div>
          <div className="metric-sub">runs predicted</div>
        </div>

        <div className="metric-card wicket">
          <div className="metric-label">Wicket Prob</div>
          <div className="metric-value">{Math.round(analytics.wicketProbability)}%</div>
          <div className="metric-sub">next over</div>
        </div>

        <div className="metric-card momentum">
          <div className="metric-label">Momentum</div>
          <div className="metric-value">{analytics.momentum}</div>
          <div className="metric-sub">{Math.round(analytics.momentumScore)}/100</div>
        </div>
      </div>

      <div className="ball-history">
        <h3>Ball by ball</h3>
        {ballHistory.map((ball, index) => (
          <div key={index} className="ball-row">
            <span className="ball-over">Over {ball.overNumber}.{ball.ballNumber}</span>
            <span className="ball-players">{ball.batsmanName} vs {ball.bowlerName}</span>
            <span className="ball-pressure">P: {Math.round(ball.pressureIndex)}</span>
            <span className={`ball-momentum ${ball.momentum.toLowerCase()}`}>{ball.momentum}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;