import React, {useEffect, useContext} from 'react';
import axios from 'axios';
import config from '../config';
import api from '../utils/api.js'
import getCookie from '../utils/cookie.js'
import apiBare from '../utils/apiBare'

const Insights = ({ data, allowInsight }) => {

  useEffect(() => {
    if (allowInsight && data) {
      const sendInsightRequest = async () => {

        try {
          const response = await api.post(
            `/insights/test_ping`,
            data,
          );
          console.log('Insights response:', response.data);
        } catch (error) {
          console.error('Error fetching insights:', error);
        }
      };

      sendInsightRequest();
    }
  }, [allowInsight, data]);

  const mockInsights = [
    { icon: '🌦️', title: 'Weather', value: '22°C Sunny' },
    { icon: '🏪', title: 'Nearby', value: '3 stores in 500m' },
    { icon: '📊', title: 'Activity', value: '35 people nearby' },
    { icon: '⚠️', title: 'Alerts', value: 'No advisories' }
  ];

  return (
    <div className="insights-container">
      <h3>📍 Location Insights</h3>
      <div className="insights-grid">
        {(data?.insights || mockInsights).map((insight, index) => (
          <div key={index} className="insight-card">
            <span className="insight-icon">{insight.icon}</span>
            <div className="insight-content">
              <h4>{insight.title}</h4>
              <p>{insight.value}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Insights;