import io from 'socket.io-client';
import {CONFIG} from '../utils/config';
import {getToken} from './api';

class SocketService {
  constructor() {
    this.socket = null;
    this.gameSocket = null;
    this.listeners = {};
  }

  // Connect to main socket
  connect = async () => {
    try {
      const token = await getToken();
      
      this.socket = io(CONFIG.SOCKET_URL, {
        auth: {
          token,
        },
        transports: ['websocket'],
        reconnection: true,
        reconnectionDelay: 1000,
        reconnectionAttempts: 5,
      });

      this.setupSocketListeners();
      
      return new Promise((resolve, reject) => {
        this.socket.on('connect', () => {
          console.log('Socket connected');
          resolve(this.socket);
        });

        this.socket.on('connect_error', error => {
          console.error('Socket connection error:', error);
          reject(error);
        });
      });
    } catch (error) {
      console.error('Socket connection failed:', error);
      throw error;
    }
  };

  // Connect to game-specific socket
  connectToGame = async (gameType, tableId) => {
    try {
      const token = await getToken();
      let gameUrl;

      switch (gameType) {
        case 'teenpatti':
          gameUrl = CONFIG.SOCKET_URL;
          break;
        case 'roulette':
          gameUrl = CONFIG.ROULETTE_URL;
          break;
        case 'ferrywheel':
          gameUrl = CONFIG.FERRYWHEEL_URL;
          break;
        default:
          gameUrl = CONFIG.SOCKET_URL;
      }

      this.gameSocket = io(gameUrl, {
        auth: {
          token,
          tableId,
          gameType,
        },
        transports: ['websocket'],
        reconnection: true,
        reconnectionDelay: 1000,
        reconnectionAttempts: 5,
      });

      this.setupGameSocketListeners();
      
      return new Promise((resolve, reject) => {
        this.gameSocket.on('connect', () => {
          console.log('Game socket connected');
          resolve(this.gameSocket);
        });

        this.gameSocket.on('connect_error', error => {
          console.error('Game socket connection error:', error);
          reject(error);
        });
      });
    } catch (error) {
      console.error('Game socket connection failed:', error);
      throw error;
    }
  };

  // Setup main socket listeners
  setupSocketListeners = () => {
    if (!this.socket) return;

    this.socket.on('disconnect', () => {
      console.log('Socket disconnected');
    });

    this.socket.on('error', error => {
      console.error('Socket error:', error);
    });

    this.socket.on('notification', data => {
      this.emit('notification', data);
    });

    this.socket.on('user_update', data => {
      this.emit('user_update', data);
    });
  };

  // Setup game socket listeners
  setupGameSocketListeners = () => {
    if (!this.gameSocket) return;

    this.gameSocket.on('disconnect', () => {
      console.log('Game socket disconnected');
    });

    this.gameSocket.on('error', error => {
      console.error('Game socket error:', error);
    });

    // Game events
    this.gameSocket.on('game_start', data => {
      this.emit('game_start', data);
    });

    this.gameSocket.on('game_update', data => {
      this.emit('game_update', data);
    });

    this.gameSocket.on('player_joined', data => {
      this.emit('player_joined', data);
    });

    this.gameSocket.on('player_left', data => {
      this.emit('player_left', data);
    });

    this.gameSocket.on('game_result', data => {
      this.emit('game_result', data);
    });

    this.gameSocket.on('table_update', data => {
      this.emit('table_update', data);
    });
  };

  // Join table
  joinTable = (tableId, betAmount) => {
    if (this.gameSocket) {
      this.gameSocket.emit('join_table', {tableId, betAmount});
    }
  };

  // Leave table
  leaveTable = () => {
    if (this.gameSocket) {
      this.gameSocket.emit('leave_table');
    }
  };

  // Make move
  makeMove = (moveData) => {
    if (this.gameSocket) {
      this.gameSocket.emit('make_move', moveData);
    }
  };

  // Send chat message
  sendChatMessage = (message) => {
    if (this.gameSocket) {
      this.gameSocket.emit('chat_message', {message});
    }
  };

  // Add event listener
  on = (event, callback) => {
    if (!this.listeners[event]) {
      this.listeners[event] = [];
    }
    this.listeners[event].push(callback);
  };

  // Remove event listener
  off = (event, callback) => {
    if (this.listeners[event]) {
      this.listeners[event] = this.listeners[event].filter(cb => cb !== callback);
    }
  };

  // Emit event to listeners
  emit = (event, data) => {
    if (this.listeners[event]) {
      this.listeners[event].forEach(callback => callback(data));
    }
  };

  // Disconnect
  disconnect = () => {
    if (this.socket) {
      this.socket.disconnect();
      this.socket = null;
    }
    if (this.gameSocket) {
      this.gameSocket.disconnect();
      this.gameSocket = null;
    }
    this.listeners = {};
  };

  // Disconnect game socket only
  disconnectGame = () => {
    if (this.gameSocket) {
      this.gameSocket.disconnect();
      this.gameSocket = null;
    }
  };
}

// Export singleton instance
const socketService = new SocketService();
export default socketService;