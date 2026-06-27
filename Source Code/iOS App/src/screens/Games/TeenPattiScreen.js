import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  StatusBar,
  Image,
} from 'react-native';
import LinearGradient from 'react-native-linear-gradient';
import Icon from 'react-native-vector-icons/Ionicons';

import {Button, Card, CoinDisplay, Loading} from '../../components';
import {socketService} from '../../services';
import {setGameType} from '../../store/slices/gameSlice';
import {useDispatch} from 'react-redux';

const TeenPattiScreen = ({navigation}) => {
  const dispatch = useDispatch();
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTables();
    dispatch(setGameType('teenpatti'));
  }, []);

  const loadTables = async () => {
    try {
      // In real app, fetch from API
      const mockTables = [
        {
          id: 1,
          name: 'طاولة المبتدئين',
          minBet: 100,
          maxBet: 1000,
          players: 3,
          maxPlayers: 5,
          status: 'waiting',
        },
        {
          id: 2,
          name: 'طاولة المحترفين',
          minBet: 1000,
          maxBet: 10000,
          players: 4,
          maxPlayers: 5,
          status: 'playing',
        },
        {
          id: 3,
          name: 'طاولة VIP',
          minBet: 5000,
          maxBet: 50000,
          players: 2,
          maxPlayers: 5,
          status: 'waiting',
        },
      ];
      setTables(mockTables);
    } catch (error) {
      console.error('Error loading tables:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinTable = (table) => {
    navigation.navigate('TableSelection', {gameType: 'teenpatti', table});
  };

  const createQuickGame = () => {
    navigation.navigate('TableSelection', {
      gameType: 'teenpatti',
      quickGame: true,
    });
  };

  return (
    <>
      <StatusBar barStyle="light-content" backgroundColor="#e60a57" />
      <LinearGradient
        colors={['#e60a57', '#ff4785']}
        style={styles.header}>
        <View style={styles.headerContent}>
          <TouchableOpacity onPress={() => navigation.goBack()}>
            <Icon name="arrow-back" size={24} color="#fff" />
          </TouchableOpacity>
          <Text style={styles.title}>Teen Patti</Text>
          <View style={styles.headerRight}>
            <CoinDisplay coins={1000} size="small" />
          </View>
        </View>
      </LinearGradient>

      <View style={styles.container}>
        <ScrollView style={styles.content} showsVerticalScrollIndicator={false}>
          {/* Quick Play */}
          <Card style={styles.quickPlayCard} onPress={createQuickGame}>
            <LinearGradient
              colors={['#4CAF50', '#45a049']}
              style={styles.quickPlayGradient}>
              <Icon name="flash" size={40} color="#fff" />
              <View style={styles.quickPlayText}>
                <Text style={styles.quickPlayTitle}>لعب سريع</Text>
                <Text style={styles.quickPlaySubtitle}>انضم للعبة فوراً</Text>
              </View>
            </LinearGradient>
          </Card>

          {/* Tables List */}
          <View style={styles.section}>
            <Text style={styles.sectionTitle">الطاولات المتاحة</Text>
            
            {loading ? (
              <Loading visible={loading} text="جاري تحميل الطاولات..." />
            ) : (
              tables.map(table => (
                <Card key={table.id} style={styles.tableCard}>
                  <View style={styles.tableHeader}>
                    <View style={styles.tableInfo}>
                      <Text style={styles.tableName}>{table.name}</Text>
                      <View style={styles.tableStatus}>
                        <View style={[
                          styles.statusDot,
                          table.status === 'playing' ? styles.statusPlaying : styles.statusWaiting
                        ]} />
                        <Text style={styles.statusText}>
                          {table.status === 'playing' ? 'قيد اللعب' : 'في الانتظار'}
                        </Text>
                      </View>
                    </View>
                    <View style={styles.playersInfo}>
                      <Icon name="people" size={16} color="#666" />
                      <Text style={styles.playersText}>
                        {table.players}/{table.maxPlayers}
                      </Text>
                    </View>
                  </View>

                  <View style={styles.tableBets}>
                    <View style={styles.betInfo}>
                      <Text style={styles.betLabel">الحد الأدنى</Text>
                      <Text style={styles.betValue}>{table.minBet}</Text>
                    </View>
                    <View style={styles.betInfo}>
                      <Text style={styles.betLabel">الحد الأقصى</Text>
                      <Text style={styles.betValue}>{table.maxBet}</Text>
                    </View>
                  </View>

                  <Button
                    title={table.status === 'playing' ? 'مشاهدة' : 'انضم'}
                    onPress={() => handleJoinTable(table)}
                    variant={table.status === 'playing' ? 'secondary' : 'primary'}
                    size="small"
                  />
                </Card>
              ))
            )}
          </View>

          {/* Game Rules */}
          <Card style={styles.rulesCard}>
            <Text style={styles.rulesTitle}>قواعد اللعبة</Text>
            <Text style={styles.rulesText}>
              • كل لاعب يحصل على 3 بطاقات{'\n'}
              • الهدف هو الحصول على أفضل يد{'\n'}
              • الترتيب من الأعلى: Pure Sequence, Sequence, Color, Pair, High Card{'\n'}
              • يمكنك الرهان أو الانسحاب في أي وقت
            </Text>
          </Card>
        </ScrollView>
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
    fontSize: 20,
    fontWeight: 'bold',
    color: '#fff',
  },
  headerRight: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    flex: 1,
    padding: 20,
  },
  quickPlayCard: {
    marginBottom: 20,
  },
  quickPlayGradient: {
    flexDirection: 'row',
    alignItems: 'center',
    padding: 20,
    borderRadius: 10,
  },
  quickPlayText: {
    marginLeft: 15,
    flex: 1,
  },
  quickPlayTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#fff',
  },
  quickPlaySubtitle: {
    fontSize: 14,
    color: 'rgba(255, 255, 255, 0.9)',
  },
  section: {
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 15,
  },
  tableCard: {
    marginBottom: 15,
  },
  tableHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 15,
  },
  tableInfo: {
    flex: 1,
  },
  tableName: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 5,
  },
  tableStatus: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  statusDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    marginRight: 5,
  },
  statusPlaying: {
    backgroundColor: '#4CAF50',
  },
  statusWaiting: {
    backgroundColor: '#FFC107',
  },
  statusText: {
    fontSize: 12,
    color: '#666',
  },
  playersInfo: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  playersText: {
    fontSize: 14,
    color: '#666',
    marginLeft: 5,
  },
  tableBets: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginBottom: 15,
  },
  betInfo: {
    alignItems: 'center',
  },
  betLabel: {
    fontSize: 12,
    color: '#888',
    marginBottom: 5,
  },
  betValue: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#e60a57',
  },
  rulesCard: {
    padding: 20,
  },
  rulesTitle: {
    fontSize: 16,
    fontWeight: 'bold',
    color: '#333',
    marginBottom: 10,
  },
  rulesText: {
    fontSize: 14,
    color: '#666',
    lineHeight: 22,
  },
});

export default TeenPattiScreen;