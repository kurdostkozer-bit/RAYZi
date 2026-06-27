import React, {useEffect, useState} from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  StatusBar,
  Alert,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Icon from 'react-native-vector-icons/Ionicons';

import {Button, Card, CoinDisplay} from '../../components';
import {socketService} from '../../services';
import {useSelector, useDispatch} from 'react-redux';
import {
  joinTableSuccess,
  leaveTable,
  updateGameData,
  updatePlayers,
} from '../../store/slices/gameSlice';

const GameRoomScreen = ({route, navigation}) => {
  const dispatch = useDispatch();
  const {gameType, tableId, betAmount} = route.params;
  const {table, players, gameData} = useSelector(state => state.game);
  const [gameState, setGameState] = useState('waiting');

  useEffect(() => {
    connectToGame();
    return () => {
      disconnectFromGame();
    };
  }, []);

  const connectToGame = async () => {
    try {
      await socketService.connectToGame(gameType, tableId);
      
      socketService.on('player_joined', handlePlayerJoined);
      socketService.on('player_left', handlePlayerLeft);
      socketService.on('game_start', handleGameStart);
      socketService.on('game_update', handleGameUpdate);
      socketService.on('game_result', handleGameResult);

      socketService.joinTable(tableId, betAmount);
    } catch (error) {
      Alert.alert('خطأ', 'فشل الاتصال باللعبة');
      navigation.goBack();
    }
  };

  const disconnectFromGame = () => {
    socketService.leaveTable();
    socketService.disconnectGame();
    dispatch(leaveTable());
  };

  const handlePlayerJoined = (data) => {
    dispatch(updatePlayers(data.players));
  };

  const handlePlayerLeft = (data) => {
    dispatch(updatePlayers(data.players));
  };

  const handleGameStart = (data) => {
    setGameState('playing');
    dispatch(updateGameData(data));
  };

  const handleGameUpdate = (data) => {
    dispatch(updateGameData(data));
  };

  const handleGameResult = (data) => {
    setGameState('finished');
    dispatch(updateGameData({result: data}));
  };

  const handleLeave = () => {
    Alert.alert(
      'مغادرة اللعبة',
      'هل أنت متأكد من مغادرة اللعبة؟',
      [
        {text: 'إلغاء', style: 'cancel'},
        {
          text: 'مغادرة',
          style: 'destructive',
          onPress: () => {
            disconnectFromGame();
            navigation.goBack();
          },
        },
      ]
    );
  };

  const renderGameContent = () => {
    switch (gameType) {
      case 'teenpatti':
        return renderTeenPattiContent();
      case 'roulette':
        return renderRouletteContent();
      case 'ferrywheel':
        return renderFerryWheelContent();
      default:
        return <Text>جاري التحميل...</Text>;
    }
  };

  const renderTeenPattiContent = () => (
    <View style={styles.gameContent}>
      <Card style={styles.playersCard}>
        <Text style={styles.playersTitle}>اللاعبون ({players.length})</Text>
        {players.map((player, index) => (
          <View key={index} style={styles.playerItem}>
            <View style={styles.playerAvatar}>
              <Text style={styles.playerAvatarText}>
                {player.name?.charAt(0) || '?'}
              </Text>
            </View>
            <Text style={styles.playerName}>{player.name || 'لاعب'}</Text>
            <CoinDisplay coins={player.coins || 0} size="small" />
          </View>
        ))}
      </Card>

      {gameState === 'playing' && (
        <Card style={styles.gameArea}>
          <Text style={styles.gameStatus}>جاري اللعب...</Text>
          <Text style={styles.roundInfo}>الجولة: {gameData?.round || 1}</Text>
        </Card>
      )}

      {gameState === 'finished' && (
        <Card style={styles.resultCard}>
          <Text style={styles.resultTitle}>النتيجة</Text>
          <Text style={styles.resultText}>
            {gameData?.result?.winner ? `الفائز: ${gameData.result.winner}` : 'انتهت اللعبة'}
          </Text>
        </Card>
      )}
    </View>
  );

  const renderRouletteContent = () => (
    <View style={styles.gameContent}>
      <Card style={styles.gameArea}>
        <Text style={styles.gameStatus}>روليت</Text>
        <Text style={styles.gameInfo}>اختر رقم أو لون للرهان</Text>
      </Card>
    </View>
  );

  const renderFerryWheelContent = () => (
    <View style={styles.gameContent}>
      <Card style={styles.gameArea}>
        <Text style={styles.gameStatus}>عجلة الحظ</Text>
        <Text style={styles.gameInfo}>جاري الدوران...</Text>
      </Card>
    </View>
  );

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.header}>
        <View style={styles.headerContent}>
          <TouchableOpacity onPress={handleLeave}>
            <Icon name="close" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.title}>
            {gameType === 'teenpatti' ? 'Teen Patti' : 
             gameType === 'roulette' ? 'Roulette' : 'عجلة الحظ'}
          </Text>
          <CoinDisplay coins={1000} size="small" />
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <View style={styles.content}>
          {renderGameContent()}

          {gameState === 'waiting' && (
            <Card style={styles.waitingCard}>
              <Text style={styles.waitingText}>في انتظار اللاعبين...</Text>
              <Text style={styles.playersCount}>{players.length}/5 لاعبين</Text>
            </Card>
          )}
        </View>
      </View>
    </>
  );
};

const styles = StyleSheet.create({
  header: {
    paddingTop: 50,
    paddingBottom: 20,
    paddingHorizontal: 20,
  },
  headerContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#fff',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  gameContent: {
    flex: 1,
  },
  playersCard: {
    padding: 15,
    marginBottom: 15,
  },
  playersTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },
  playerItem: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
  },
  playerAvatar: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: '#e60a57',
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 12,
  },
  playerAvatarText: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#fff',
  },
  playerName: {
    flex: 1,
    fontSize: 14,
    color: '#333',
  },
  gameArea: {
    padding: 20,
    alignItems: 'center',
    marginBottom: 15,
  },
  gameStatus: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },
  gameInfo: {
    fontSize: 14,
    color: '#666',
  },
  roundInfo: {
    fontSize: 14,
    color: '#888',
    marginTop: 5,
  },
  resultCard: {
    padding: 20,
    alignItems: 'center',
  },
  resultTitle: {
    fontSize: 20,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },
  resultText: {
    fontSize: 16,
    color: '#e60a57',
    fontWeight: '600',
  },
  waitingCard: {
    padding: 20,
    alignItems: 'center',
  },
  waitingText: {
    fontSize: 16,
    color: '#666',
  },
  playersCount: {
    fontSize: 14,
    color: '#888',
    marginTop: 5,
  },
});

export default GameRoomScreen;