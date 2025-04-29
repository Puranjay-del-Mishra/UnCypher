const getApiBaseUrl = () => {
  const hostname = window.location.hostname;

  if (hostname === 'localhost') {
    return 'http://localhost:8080';  // Local backend
  } else if (hostname === 'staging.UnCypher.com') {
    return 'https://staging-api.UnCypher.com';  // Staging environment
  } else {
    return 'https://api.UnCypher.com';  // Production backend
  }
};

const config = {
  API_BASE_URL: getApiBaseUrl(),
  APP_NAME: "UnCypher",
  FEATURE_FLAG: false,  // Example of toggling features based on environment
};

export default config;
