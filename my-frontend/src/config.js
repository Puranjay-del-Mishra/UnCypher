const getApiBaseUrl = () => {
  const hostname = window.location.hostname;

  if (hostname === 'localhost') {
    return 'http://localhost:8080';  // Local backend
  } else if (hostname === 'staging.DrawGPT.com') {
    return 'https://staging-api.DrawGPT.com';  // Staging environment
  } else {
    return 'https://api.DrawGPT.com';  // Production backend
  }
};

const config = {
  API_BASE_URL: getApiBaseUrl(),
  APP_NAME: "DrawGPT",
  FEATURE_FLAG: false,  // Example of toggling features based on environment
};

export default config;
