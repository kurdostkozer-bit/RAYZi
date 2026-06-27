// Environment Configuration
const ENV = {
  development: {
    API_BASE_URL: 'http://localhost:5000',
    SOCKET_URL: 'http://localhost:5001',
    ROULETTE_URL: 'http://localhost:5002',
    FERRYWHEEL_URL: 'http://localhost:5003',
    SHARED_SECRET_KEY: '5TIvw5cpc0',
    JWT_SECRET: '2FhKmINItB',
  },
  production: {
    API_BASE_URL: process.env.API_BASE_URL || 'https://your-admin-domain.com',
    SOCKET_URL: process.env.SOCKET_URL || 'https://your-teenpatti-domain.com',
    ROULETTE_URL: process.env.ROULETTE_URL || 'https://your-roulette-domain.com',
    FERRYWHEEL_URL: process.env.FERRYWHEEL_URL || 'https://your-ferrywheel-domain.com',
    SHARED_SECRET_KEY: process.env.SHARED_SECRET_KEY || '5TIvw5cpc0',
    JWT_SECRET: process.env.JWT_SECRET || '2FhKmINItB',
  },
};

const getEnvVars = () => {
  const env = __DEV__ ? ENV.development : ENV.production;
  return {
    ...env,
    IS_DEV: __DEV__,
    APP_VERSION: '1.0.0',
  };
};

export const CONFIG = getEnvVars();

// Game Configuration
export const GAME_CONFIG = {
  MIN_COINS_TO_PLAY: 100,
  MAX_TABLE_BET: 10000,
  DEFAULT_COINS: 1000,
  BONUS_COINS: 500,
  TABLE_TIME_LIMIT: 60, // seconds
  AUTO_PLAY_TIME: 15, // seconds
  MAX_PLAYERS_PER_TABLE: 5,
};

// Feature Flags
export const FEATURES = {
  TEENPATTI: process.env.ENABLE_TEENPATTI !== 'false',
  ROULETTE: process.env.ENABLE_ROULETTE !== 'false',
  FERRYWHEEL: process.env.ENABLE_FERRYWHEEL !== 'false',
  LIVE_GAMES: process.env.ENABLE_LIVE_GAMES !== 'false',
  CHAT: process.env.ENABLE_CHAT !== 'false',
  VOICE_CHAT: process.env.ENABLE_VOICE_CHAT === 'true',
  FACEBOOK_LOGIN: process.env.ENABLE_FACEBOOK_LOGIN !== 'false',
  GOOGLE_LOGIN: process.env.ENABLE_GOOGLE_LOGIN !== 'false',
  APPLE_LOGIN: process.env.ENABLE_APPLE_LOGIN !== 'false',
};

// Social Configuration
export const SOCIAL_CONFIG = {
  FACEBOOK_APP_ID: process.env.FACEBOOK_APP_ID || '',
  GOOGLE_WEB_CLIENT_ID: process.env.GOOGLE_WEB_CLIENT_ID || '',
};

// App Configuration
export const APP_CONFIG = {
  SUPPORT_EMAIL: process.env.SUPPORT_EMAIL || 'support@rayzi.com',
  WEBSITE_URL: process.env.WEBSITE_URL || 'https://rayzi.com',
  PRIVACY_POLICY_URL: `${process.env.WEBSITE_URL || 'https://rayzi.com'}/privacy`,
  TERMS_OF_SERVICE_URL: `${process.env.WEBSITE_URL || 'https://rayzi.com'}/terms`,
};

export default CONFIG;